package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarvelViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home)
    object Members : Screen("members", "Members", Icons.Filled.People, Icons.Outlined.People)
    object Expiry : Screen("expiry", "Expiry", Icons.Filled.AccessTime, Icons.Outlined.AccessTime)
    object Payments : Screen("payments", "Payments", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    object Plans : Screen("plans", "Plans", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)
}

@Composable
fun MarvelApp(
    viewModel: MarvelViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val urgentExpiring by viewModel.urgentExpiringMembers.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    val screens = listOf(
        Screen.Dashboard,
        Screen.Members,
        Screen.Expiry,
        Screen.Payments,
        Screen.Plans
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarvelBlack,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MarvelSurfaceElevated,
                    contentColor = TextWhite,
                    actionColor = MarvelRedLight
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MarvelDark,
                tonalElevation = 8.dp,
                windowInsets = NavigationBarDefaults.windowInsets,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (screen == Screen.Expiry && urgentExpiring.isNotEmpty()) {
                                        Badge(
                                            containerColor = MarvelRed,
                                            contentColor = TextWhite
                                        ) {
                                            Text(urgentExpiring.size.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MarvelRed,
                            selectedTextColor = MarvelRedLight,
                            indicatorColor = MarvelRed.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToMembers = {
                        navController.navigate(Screen.Members.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToExpiry = {
                        navController.navigate(Screen.Expiry.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToPlans = {
                        navController.navigate(Screen.Plans.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToPayments = {
                        navController.navigate(Screen.Payments.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Members.route) {
                MembersScreen(viewModel = viewModel)
            }

            composable(Screen.Expiry.route) {
                ExpiryScreen(viewModel = viewModel)
            }

            composable(Screen.Payments.route) {
                PaymentsScreen(viewModel = viewModel)
            }

            composable(Screen.Plans.route) {
                PlansScreen(
                    viewModel = viewModel,
                    onNavigateToMembersWithPlan = {
                        navController.navigate(Screen.Members.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
