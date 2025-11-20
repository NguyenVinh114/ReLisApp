package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.relisapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseAdminScreen(
    title: String,
    // Optional: Parameter to identify current screen for highlighting
    currentScreen: String = "",
    onManageCategories: () -> Unit,
    onManageLessons: () -> Unit,
    onManageUsers: () -> Unit,
    onFeedback: () -> Unit,
    onLogout: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Helper function to close drawer and perform action
    fun onNavClick(action: () -> Unit) {
        scope.launch {
            drawerState.close()
            action()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(300.dp) // Moderate menu width
            ) {
                // 1. Menu Header (Admin Info)
                AdminDrawerHeader()

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // 2. Menu Items (Using M3 NavigationDrawerItem)
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(
                        text = "Management",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    AdminDrawerItem(
                        label = "Categories",
                        icon = R.drawable.ic_category,
                        // Highlight if title matches (Ensure you pass "Manage Categories" as title)
                        selected = title == "Manage Categories",
                        onClick = { onNavClick(onManageCategories) }
                    )

                    AdminDrawerItem(
                        label = "Lessons",
                        icon = R.drawable.ic_assignment,
                        selected = title == "Manage Lessons",
                        onClick = { onNavClick(onManageLessons) }
                    )

                    AdminDrawerItem(
                        label = "Users",
                        icon = R.drawable.ic_user,
                        selected = title == "Manage Users",
                        onClick = { onNavClick(onManageUsers) }
                    )

                    AdminDrawerItem(
                        label = "Feedback",
                        icon = R.drawable.ic_feedback,
                        selected = title == "Feedback",
                        onClick = { onNavClick(onFeedback) }
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Push bottom items down



                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    AdminDrawerItem(
                        label = "Logout",
                        icon = R.drawable.ic_logout,
                        selected = false,
                        onClick = { onNavClick(onLogout) },
                        color = MaterialTheme.colorScheme.error // Red color for logout
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    // Right action (e.g., small avatar or notification)
                    actions = {
                        IconButton(onClick = { /* Todo: Profile setting */ }) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Background for main content to separate visual layers
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                content(Modifier.padding(16.dp)) // Default padding for inner content
            }
        }
    }
}

// --- HELPER COMPONENTS FOR CLEAN CODE ---

@Composable
fun AdminDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            // Simulated Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A", // First letter of Admin name
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Administrator",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "admin@relisapp.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun AdminDrawerItem(
    label: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    NavigationDrawerItem(
        label = { Text(text = label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp), // ✅ giới hạn kích thước
                tint = if (selected) MaterialTheme.colorScheme.primary else color
            )
        },
        modifier = Modifier.padding(vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = color,
            unselectedIconColor = color
        ),
        shape = MaterialTheme.shapes.medium // Rounded corners for item
    )
}