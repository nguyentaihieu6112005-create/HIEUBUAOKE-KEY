package com.hieubuaoke.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hieubuaoke.app.ui.theme.HieuBuaokeColors

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("NHẬP", "TÀI NGUYÊN", "NHẬT KÝ", "XUẤT")

    Scaffold(
        containerColor = HieuBuaokeColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = HieuBuaokeColors.NeonGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "HIEUBUAOKE",
                                color = HieuBuaokeColors.NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "BỘ CÔNG CỤ ASSET UNITY",
                                color = HieuBuaokeColors.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = HieuBuaokeColors.Card2,
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, HieuBuaokeColors.NeonGreen.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "VIP • VĨNH VIỄN",
                            color = HieuBuaokeColors.NeonGreen,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HieuBuaokeColors.Background)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = HieuBuaokeColors.Background2,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, label ->
                    val selected = index == selectedTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = index },
                        icon = {
                            val icon = when (index) {
                                0 -> Icons.Default.UploadFile
                                1 -> Icons.Default.FolderOpen
                                2 -> Icons.Default.History
                                else -> Icons.Default.Download
                            }
                            Icon(icon, contentDescription = label)
                        },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            selectedIndicatorColor = HieuBuaokeColors.NeonGreen,
                            unselectedIconColor = HieuBuaokeColors.TextSecondary,
                            unselectedTextColor = HieuBuaokeColors.TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatusCard() }
            item { SectionHeader("NHẬP FILE") }
            item {
                ActionCard(
                    title = "CHỌN FILE UNITY",
                    subtitle = "AssetBundle / SerializedFile / .assets / .bundle",
                    icon = Icons.Default.UploadFile
                )
            }
            item { SectionHeader("TÀI NGUYÊN GẦN ĐÂY") }
            items(
                listOf(
                    AssetLine(type = "ẢNH", name = "minimap_NeoParadise_VN", meta = "Texture2D • 1024 × 1024"),
                    AssetLine(type = "MATERIAL", name = "Mat_NeoParadise", meta = "Material • 2.1 MB"),
                    AssetLine(type = "MESH", name = "Building_01", meta = "Mesh • 5.6 MB")
                )
            ) { asset ->
                AssetCard(asset)
            }
        }
    }
}

@Composable
private fun StatusCard() {
    Surface(
        color = HieuBuaokeColors.Card,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, HieuBuaokeColors.NeonGreen.copy(alpha = 0.30f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(HieuBuaokeColors.NeonGreen, shape = RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "● Sẵn sàng • 2 entry • 2 object",
                color = HieuBuaokeColors.TextPrimary
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = HieuBuaokeColors.Cyan,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun ActionCard(title: String, subtitle: String, icon: ImageVector) {
    Surface(
        color = HieuBuaokeColors.Card,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, HieuBuaokeColors.Cyan.copy(alpha = 0.30f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HieuBuaokeColors.Cyan,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = HieuBuaokeColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = HieuBuaokeColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private data class AssetLine(
    val type: String,
    val name: String,
    val meta: String
)

@Composable
private fun AssetCard(asset: AssetLine) {
    Surface(
        color = HieuBuaokeColors.Card,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, HieuBuaokeColors.Divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(HieuBuaokeColors.Card2, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = asset.type.first().toString(),
                    color = HieuBuaokeColors.NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    color = HieuBuaokeColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = asset.meta,
                    color = HieuBuaokeColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = asset.type,
                color = HieuBuaokeColors.Cyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}
