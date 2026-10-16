# HIEUBUAOKE — module Đóng gói & Tải xuống

Đây là module chạy thật với UnityPy cho luồng: chọn file → phân tích object → chọn Texture2D → thay ảnh → serialize lại bằng UnityPy → mở lại output bằng UnityPy để xác minh → hiển thị tên/dung lượng/hash → tải xuống.

## Cấu trúc
- `app.py`: FastAPI API, dùng UnityPy parser thật.
- `requirements.txt`: dependency.
- `static/index.html`: giao diện neon tiếng Việt, dùng API để xử lý.

## Chạy thử trên máy có Python
```bash
python -m venv .venv
# Windows: .venv\Scripts\activate
# Linux/macOS/Termux: source .venv/bin/activate
pip install -r requirements.txt
uvicorn app:app --host 127.0.0.1 --port 8000
```
Mở `static/index.html` bằng web server cùng origin hoặc chạy:
```bash
python -m http.server 8080 --directory static
```
Sau đó nhập API URL `http://127.0.0.1:8000` vào giao diện. Nếu mở trang từ thiết bị khác, `127.0.0.1` là chính thiết bị đang mở trang, không phải máy chủ API. Cần thay bằng địa chỉ API có thể truy cập từ thiết bị đó.

## Triển khai thật
- GitHub Pages chỉ host frontend tĩnh, không chạy Python/UnityPy.
- Deploy `app.py` lên một Python host có hỗ trợ file upload; đặt `ALLOWED_ORIGINS` thành origin frontend chính xác, ví dụ `https://TEN.github.io`.
- Không dùng `allow_origins=["*"]` trong production. Mặc định trong module chỉ cho localhost.
- Đặt `MAX_UPLOAD_BYTES` phù hợp với RAM/timeout của host.
- Dữ liệu upload được xử lý trong request; file output tạm nằm trong thư mục hệ thống `hieubuaoke_outputs`. Cần thêm chính sách dọn file tạm theo lịch khi deploy lâu dài.
- Không đặt token/secret trong frontend.

## Giới hạn quan trọng
- Module hiện thực hóa thay Texture2D bằng ảnh qua UnityPy; không tự nhận là hỗ trợ thay Mesh/Material/Shader hay mọi biến thể AssetBundle.
- `env.file.save()` và texture encoding phụ thuộc phiên bản UnityPy, codec, kiểu texture và cấu trúc bundle. Nếu parser không thể serialize hoặc mở lại output, API trả lỗi và frontend không cho tải file.
- Mở lại bằng UnityPy là kiểm tra cấu trúc cơ bản, không chứng minh bundle sẽ chạy trong mọi game/Unity build. Hãy thử trên bản sao trong môi trường tương thích.
- File gốc trong trình duyệt không bị ghi đè. Kết quả tải xuống là một file mới.
- Chưa tích hợp vào repository của người dùng và chưa kiểm thử bằng AssetBundle thực do chưa có file mẫu/repository source hiện tại trong lượt này.

## API
- `GET /health`
- `POST /api/inspect` — multipart field `file`
- `POST /api/replace-texture` — multipart fields `file`, `path_id`, `replacement`

Mở `/docs` trên API để xem schema OpenAPI.
