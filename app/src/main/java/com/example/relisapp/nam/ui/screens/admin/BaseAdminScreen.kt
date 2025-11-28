package com.example.relisapp.nam.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
    onLogout: () -> Unit,              // ⭐ THÊM THAM SỐ LOGOUT
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Text(
                        "ReLis Learning",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                // All menu tabs
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

                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                // ⭐ LOGOUT ITEM
                NavigationDrawerItem(
                    label = { Text("Logout", fontWeight = FontWeight.Bold, color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()          // ⭐ GỌI HÀM LOGOUT
                    },
                    icon = {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        // MAIN SCAFFOLD
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onUserIconClick) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    UserTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = {
                                Text(
                                    tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
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