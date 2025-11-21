package com.example.relisapp.phat.ui.user.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseUserScreen(
    title: String,
    currentTab: UserTab,
    onTabSelected: (UserTab) -> Unit,
    onUserIconClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 1. MENU TRƯỢT (DRAWER)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White // Nền Drawer trắng sạch
            ) {
                // Header của Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary) // Nền xanh chủ đạo
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Text(
                        "ReLis Learning",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Các mục Menu
                UserTab.entries.forEach { tab ->
                    NavigationDrawerItem(
                        label = { Text(tab.title, fontWeight = FontWeight.Medium) },
                        selected = currentTab == tab,
                        onClick = {
                            onTabSelected(tab)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) {
        // 2. GIAO DIỆN CHÍNH (SCAFFOLD)
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background, // Nền xám nhạt toàn màn hình

            // --- TOP BAR ---
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    // Nút Menu Trái
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    // Nút User Phải
                    actions = {
                        IconButton(onClick = onUserIconClick) {
                            // Surface tròn bao quanh icon User
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = Color.White.copy(alpha = 0.2f), // Nền mờ nhẹ
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountCircle,
                                        contentDescription = "User Profile",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Màu Xanh tươi
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },

            // --- BOTTOM BAR ---
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    UserTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            selected = isSelected,
                            onClick = { onTabSelected(tab) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                unselectedIconColor = Color.Gray
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

// Enum định nghĩa các tab
enum class UserTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    CATEGORIES("Categories", Icons.AutoMirrored.Filled.List),
    LESSON("My Lessons", Icons.Filled.PlayArrow),
    PROFILE("Profile", Icons.Filled.Person)
}