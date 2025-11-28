package com.example.relisapp.nam.ui.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.relisapp.nam.database.Converters.BitmapConverter
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.vector.ImageVector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    users: List<User>,
    onViewDetail: (User) -> Unit,
    onToggleLock: (User) -> Unit,
    onChangeRole: (User) -> Unit,
    onDeleteUser: (User) -> Unit,
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false,
    currentUser: User?,
    onBack: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var search by remember { mutableStateOf("") }
    var filterRole by remember { mutableStateOf("Tất cả") }
    var filterStatus by remember { mutableStateOf("Tất cả") }
    var sortBy by remember { mutableStateOf("username") } // username, date, role
    var showSortMenu by remember { mutableStateOf(false) }



    // Lọc và sắp xếp
    val filtered = users.filter { user ->
        (search.isBlank() || user.username.contains(search, ignoreCase = true) ||
                user.fullName?.contains(search, ignoreCase = true) == true) &&
                (filterRole == "Tất cả" || user.role == filterRole) &&
                (filterStatus == "Tất cả" ||
                        (filterStatus == "Active" && user.accountStatus == "active") ||
                        (filterStatus == "Locked" && user.accountStatus == "locked"))
    }.let { list ->
        when (sortBy) {
            "username" -> list.sortedBy { it.username }
            "role" -> list.sortedBy { it.role }
            "date" -> list.sortedByDescending { it.createdAt }
            else -> list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quản lý người dùng", fontSize = 20.sp)
                        Text(
                            "${filtered.size}/${users.size} người dùng",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Sort button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, "Sắp xếp")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Theo tên") },
                                onClick = { sortBy = "username"; showSortMenu = false },
                                leadingIcon = {
                                    if (sortBy == "username") Icon(Icons.Default.Check, null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Theo vai trò") },
                                onClick = { sortBy = "role"; showSortMenu = false },
                                leadingIcon = {
                                    if (sortBy == "role") Icon(Icons.Default.Check, null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Theo ngày tạo") },
                                onClick = { sortBy = "date"; showSortMenu = false },
                                leadingIcon = {
                                    if (sortBy == "date") Icon(Icons.Default.Check, null)
                                }
                            )
                        }
                    }

                    // Refresh button
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, "Làm mới")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Loading indicator
            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = BluePrimary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Statistics Summary
                StatisticsSummary(
                    total = users.size,
                    active = users.count { it.accountStatus == "active" },
                    locked = users.count { it.accountStatus == "locked" },
                    admins = users.count { it.role == "admin" }
                )

                Spacer(Modifier.height(16.dp))

                // SEARCH
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Tìm kiếm username hoặc tên...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = "" }) {
                                Icon(Icons.Default.Clear, "Xóa")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary
                    )
                )

                Spacer(Modifier.height(12.dp))

                // FILTERS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        label = "Vai trò",
                        options = listOf("Tất cả", "user", "admin", "mod"),
                        selected = filterRole,
                        onSelect = { filterRole = it },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        label = "Trạng thái",
                        options = listOf("Tất cả", "Active", "Locked"),
                        selected = filterStatus,
                        onSelect = { filterStatus = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // USER LIST or EMPTY STATE
                when {
                    users.isEmpty() -> {
                        EmptyState(
                            message = "Chưa có người dùng nào",
                            icon = Icons.Default.PersonOff
                        )
                    }
                    filtered.isEmpty() -> {
                        EmptyState(
                            message = "Không tìm thấy người dùng phù hợp",
                            icon = Icons.Default.SearchOff
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filtered, key = { it.userId }) { user ->

                                EnhancedUserCard(

                                    user = user,
                                    onViewDetail = { onViewDetail(user) },
                                    onToggleLock = { onToggleLock(user) },
                                    onChangeRole = { onChangeRole(user) },
                                    onDeleteUser = { onDeleteUser(user)}
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsSummary(
    total: Int,
    active: Int,
    locked: Int,
    admins: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Tổng số", value = total, icon = Icons.Default.People)
            StatItem(label = "Hoạt động", value = active, icon = Icons.Default.CheckCircle, color = Color(0xFF2E7D32))
            StatItem(label = "Bị khóa", value = locked, icon = Icons.Default.Lock, color = Color(0xFFC62828))
            StatItem(label = "Quản trị", value = admins, icon = Icons.Default.AdminPanelSettings, color = Color(0xFF1565C0))
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: Int,
    icon: ImageVector,
    color: Color = BluePrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = GrayText
        )
    }
}

@Composable
fun EnhancedUserCard(
    user: User,
    onViewDetail: () -> Unit,
    onToggleLock: () -> Unit,
    onChangeRole: () -> Unit,
    onDeleteUser: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val avatarBitmap = user.avatar?.let { BitmapConverter.byteArrayToBitmap(it) }

                    Surface(
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap.asImageBitmap(),
                                contentDescription = "Avatar của ${user.username}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(BluePrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "No avatar",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }


                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            user.username,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!user.fullName.isNullOrBlank()) {
                            Text(
                                user.fullName,
                                fontSize = 14.sp,
                                color = GrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!user.email.isNullOrBlank()) {
                            Text(
                                user.email,
                                fontSize = 12.sp,
                                color = GrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Menu button
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Thêm tùy chọn")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Xem chi tiết") },
                            onClick = { onViewDetail(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.RemoveRedEye, null) }
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                        DropdownMenuItem(
                            text = { Text(if (user.accountStatus == "locked") "Mở khóa" else "Khóa tài khoản") },
                            onClick = { showConfirmDialog = true; showMenu = false },
                            leadingIcon = {
                                Icon(
                                    if (user.accountStatus == "locked") Icons.Default.LockOpen else Icons.Default.Lock,
                                    null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Phân quyền") },
                            onClick = { onChangeRole(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Xóa người dùng", color = Color.Red, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showConfirmDeleteDialog = true   // ⭐ Mở dialog
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RoleBadge(role = user.role)
                StatusBadge(status = user.accountStatus)
            }

            // Created date
            user.createdAt?.let { date ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = GrayText
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Tạo: ${formatDate(date)}",
                        fontSize = 12.sp,
                        color = GrayText
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    if (user.accountStatus == "locked") Icons.Default.LockOpen else Icons.Default.Lock,
                    null
                )
            },
            title = { Text("Xác nhận") },
            text = {
                Text(
                    if (user.accountStatus == "locked")
                        "Bạn có chắc muốn mở khóa tài khoản \"${user.username}\"?"
                    else
                        "Bạn có chắc muốn khóa tài khoản \"${user.username}\"? Người dùng sẽ không thể đăng nhập."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleLock()
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.accountStatus == "locked")
                            Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                ) {
                    Text(if (user.accountStatus == "locked") "Mở khóa" else "Khóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
            title = { Text("Xóa người dùng") },
            text = {
                Text("Bạn có chắc chắn muốn xóa tài khoản \"${user.username}\"? Hành động này KHÔNG thể hoàn tác.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser()
                        showConfirmDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun RoleBadge(role: String?) {
    val (bgColor, textColor, icon) = when(role) {
        "admin" -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), Icons.Default.AdminPanelSettings)
        "mod" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.VerifiedUser)
        "user" -> Triple(Color(0xFFF3E5F5), Color(0xFF6A1B9A), Icons.Default.Person)
        else -> Triple(Color.LightGray, Color.DarkGray, Icons.Default.Person)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Text(
                text = role?.uppercase() ?: "N/A",
                fontSize = 12.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(status: String?) {
    val (bgColor, textColor, icon) = when(status) {
        "active" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "locked" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Lock)
        else -> Triple(Color.LightGray, Color.DarkGray, Icons.AutoMirrored.Filled.Help)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor
            )
            Text(
                text = when(status) {
                    "active" -> "HOẠT ĐỘNG"
                    "locked" -> "BỊ KHÓA"
                    else -> "N/A"
                },
                fontSize = 12.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (selected != "Tất cả")
                    BluePrimary.copy(alpha = 0.1f) else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$label: $selected",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    },
                    leadingIcon = {
                        if (opt == selected) {
                            Icon(Icons.Default.Check, null, tint = BluePrimary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = GrayText.copy(alpha = 0.5f)
            )
            Text(
                message,
                fontSize = 16.sp,
                color = GrayText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatDate(dateString: String): String {
    try {
        // Trường hợp 1: Nếu chuỗi lưu trữ là timestamp (dạng số long, ví dụ "1678992000000")
        val timestamp = dateString.toLongOrNull()
        if (timestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        // Trường hợp 2: Nếu chuỗi đã là định dạng ngày tháng (ví dụ ISO 8601),
        // bạn có thể parse nó ở đây. Hiện tại ta sẽ trả về nguyên gốc nếu không phải timestamp.
        return dateString
    } catch (_: Exception) {
        return dateString // Trả về chuỗi gốc nếu có lỗi xảy ra
    }
}