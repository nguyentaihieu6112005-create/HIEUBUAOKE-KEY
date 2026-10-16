from __future__ import annotations

import hashlib
import io
import os
import re
import tempfile
from pathlib import Path
from typing import Any

import UnityPy
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from PIL import Image, UnidentifiedImageError

APP_NAME = "HIEUBUAOKE AssetBundle Editor"
MAX_UPLOAD_BYTES = int(os.getenv("MAX_UPLOAD_BYTES", str(150 * 1024 * 1024)))
OUTPUT_DIR = Path(os.getenv("OUTPUT_DIR", tempfile.gettempdir())) / "hieubuaoke_outputs"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

app = FastAPI(title=APP_NAME, version="0.1.0")

# Set ALLOWED_ORIGINS to the exact deployed frontend origin(s) in production.
# Example: https://yourname.github.io
allowed_origins = [
    item.strip()
    for item in os.getenv("ALLOWED_ORIGINS", "http://localhost:8000,http://127.0.0.1:8000").split(",")
    if item.strip()
]
app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=False,
    allow_methods=["GET", "POST"],
    allow_headers=["Content-Type"],
    expose_headers=["Content-Disposition", "X-Output-Size", "X-Output-SHA256", "X-Output-Name", "X-Original-Size", "X-Original-SHA256", "X-Texture-Path-ID", "X-Texture-Width", "X-Texture-Height", "X-Original-Texture-Width", "X-Original-Texture-Height", "X-Parser-Verified"],
)


def safe_filename(name: str | None, fallback: str = "assetbundle") -> str:
    name = Path(name or fallback).name
    name = re.sub(r"[^A-Za-z0-9._-]+", "_", name).strip("._")
    return (name or fallback)[:150]


async def read_upload_limited(upload: UploadFile) -> bytes:
    data = await upload.read(MAX_UPLOAD_BYTES + 1)
    if not data:
        raise HTTPException(400, "File tải lên đang trống.")
    if len(data) > MAX_UPLOAD_BYTES:
        raise HTTPException(413, f"File vượt giới hạn {MAX_UPLOAD_BYTES} byte.")
    return data


def load_unity(data: bytes):
    try:
        env = UnityPy.load(data)
        # Iterating objects forces parser access and catches many malformed files early.
        objects = list(env.objects)
        return env, objects
    except Exception as exc:
        raise HTTPException(
            status_code=422,
            detail=f"Không phân tích được file bằng UnityPy: {type(exc).__name__}: {exc}",
        ) from exc


def object_summary(obj: Any) -> dict[str, Any]:
    item: dict[str, Any] = {
        "path_id": str(getattr(obj, "path_id", "")),
        "class": str(getattr(getattr(obj, "type", None), "name", "Unknown")),
        "byte_size": getattr(obj, "byte_size", None),
        "name": "",
        "editable": False,
    }
    try:
        data = obj.read()
        item["name"] = str(getattr(data, "m_Name", "") or getattr(data, "name", "") or "")
        item["editable"] = item["class"] in {
            "Texture2D", "TextAsset", "Mesh", "Material", "MonoBehaviour"
        }
        if item["class"] == "Texture2D":
            item["width"] = int(getattr(data, "m_Width", 0) or 0)
            item["height"] = int(getattr(data, "m_Height", 0) or 0)
            item["format"] = str(getattr(data, "m_TextureFormat", "unknown"))
            item["mipmap_count"] = int(getattr(data, "m_MipCount", 0) or 0)
    except Exception as exc:
        item["read_warning"] = f"{type(exc).__name__}: {exc}"
    if not item["name"]:
        item["name"] = f'{item["class"]}_{item["path_id"]}'
    return item


@app.get("/health")
def health():
    return {"ok": True, "parser": "UnityPy", "app": APP_NAME, "max_upload_bytes": MAX_UPLOAD_BYTES}


@app.post("/api/inspect")
async def inspect_bundle(file: UploadFile = File(...)):
    data = await read_upload_limited(file)
    env, objects = load_unity(data)
    items = [object_summary(obj) for obj in objects]
    unity_version = getattr(env, "version", None)
    return {
        "original_name": safe_filename(file.filename),
        "original_size_bytes": len(data),
        "original_sha256": hashlib.sha256(data).hexdigest(),
        "unity_version": str(unity_version) if unity_version else None,
        "object_count": len(items),
        "objects": items,
        "parser": "UnityPy",
        "note": "Danh sách được đọc từ parser thực tế; object không đọc được có read_warning.",
    }


@app.post("/api/replace-texture")
async def replace_texture(
    file: UploadFile = File(...),
    path_id: str = Form(...),
    replacement: UploadFile = File(...),
):
    original = await read_upload_limited(file)
    replacement_bytes = await read_upload_limited(replacement)

    try:
        with Image.open(io.BytesIO(replacement_bytes)) as im:
            replacement_image = im.convert("RGBA")
            replacement_image.load()
    except (UnidentifiedImageError, OSError) as exc:
        raise HTTPException(400, "Ảnh thay thế không hợp lệ. Hãy chọn PNG/JPEG mà Pillow đọc được.") from exc

    env, objects = load_unity(original)
    target = None
    for obj in objects:
        if str(getattr(obj, "path_id", "")) == str(path_id):
            if str(getattr(getattr(obj, "type", None), "name", "")) != "Texture2D":
                raise HTTPException(400, "Path ID đã chọn không phải Texture2D.")
            target = obj
            break
    if target is None:
        raise HTTPException(404, "Không tìm thấy Texture2D với Path ID đã chọn.")

    try:
        texture_data = target.read()
        expected_w = int(getattr(texture_data, "m_Width", 0) or 0)
        expected_h = int(getattr(texture_data, "m_Height", 0) or 0)
        # UnityPy handles conversion/encoding according to the texture object's supported format.
        texture_data.image = replacement_image
        texture_data.save()
        output = env.file.save()
        if not isinstance(output, (bytes, bytearray)) or not output:
            raise RuntimeError("UnityPy không trả về dữ liệu đầu ra.")
        output = bytes(output)

        # Re-open output with the same real parser before exposing it for download.
        check_env, check_objects = load_unity(output)
        check_target = next(
            (obj for obj in check_objects
             if str(getattr(obj, "path_id", "")) == str(path_id)
             and str(getattr(getattr(obj, "type", None), "name", "")) == "Texture2D"),
            None,
        )
        if check_target is None:
            raise RuntimeError("Kiểm tra sau đóng gói không tìm thấy Texture2D đã sửa.")
        check_data = check_target.read()
        actual_w = int(getattr(check_data, "m_Width", 0) or 0)
        actual_h = int(getattr(check_data, "m_Height", 0) or 0)
        if actual_w <= 0 or actual_h <= 0:
            raise RuntimeError("Texture đầu ra có kích thước không hợp lệ.")
    except HTTPException:
        raise
    except Exception as exc:
        raise HTTPException(
            status_code=422,
            detail=f"Không thể thay texture/đóng gói hoặc xác minh đầu ra: {type(exc).__name__}: {exc}",
        ) from exc

    original_name = safe_filename(file.filename, "assetbundle")
    stem = Path(original_name).stem or "assetbundle"
    suffix = Path(original_name).suffix or ".bundle"
    output_name = safe_filename(f"{stem}_edited{suffix}")
    output_path = OUTPUT_DIR / f"{hashlib.sha256(output).hexdigest()[:16]}_{output_name}"
    output_path.write_bytes(output)

    digest = hashlib.sha256(output).hexdigest()
    media_type = "application/octet-stream"
    headers = {
        "Content-Disposition": f'attachment; filename="{output_name}"',
        "X-Output-Name": output_name,
        "X-Output-Size": str(len(output)),
        "X-Output-SHA256": digest,
        "X-Original-Size": str(len(original)),
        "X-Original-SHA256": hashlib.sha256(original).hexdigest(),
        "X-Texture-Path-ID": str(path_id),
        "X-Texture-Width": str(actual_w),
        "X-Texture-Height": str(actual_h),
        "X-Original-Texture-Width": str(expected_w),
        "X-Original-Texture-Height": str(expected_h),
        "X-Parser-Verified": "true",
    }
    return FileResponse(
        path=output_path,
        media_type=media_type,
        filename=output_name,
        headers=headers,
        background=None,
    )
