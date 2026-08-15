package com.findyourpet.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.ui.components.BottomPrimaryActionBanner
import com.findyourpet.app.ui.components.BottomNavigationDestination
import com.findyourpet.app.ui.screens.AuthScreen
import com.findyourpet.app.ui.screens.ActivityScreen
import com.findyourpet.app.ui.screens.CreatePetPostScreen
import com.findyourpet.app.ui.screens.HomeScreen
import com.findyourpet.app.ui.screens.NotificationsScreen
import com.findyourpet.app.ui.screens.ProfileScreen
import com.findyourpet.app.ui.screens.SightingAlertScreen
import com.findyourpet.app.ui.screens.SightingDetailScreen
import com.findyourpet.app.ui.theme.MascotasPerdidasTheme
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel

class MainActivity : ComponentActivity() {
    private val petViewModel: PetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MascotasPerdidasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PetAppNavigation(viewModel = petViewModel)
                }
            }
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_CREATE = "create"
private const val ROUTE_PROFILE = "profile"
private const val ROUTE_ACTIVITY = "activity"
private const val ROUTE_NOTIFICATIONS = "notifications"
private const val ROUTE_ALERT = "alert/{postId}"
private const val ROUTE_SIGHTING_DETAIL = "sighting/{sightingId}"
private const val NOTIFICATION_TAG = "NotificationRouting"
private const val ACTIVITY_TAG = "ActivityRouting"

@Composable
fun PetAppNavigation(viewModel: PetViewModel) {
    val authState by viewModel.authState.collectAsState()

    val signedInState = authState as? AuthUiState.SignedIn
    if (signedInState == null) {
        AuthScreen(viewModel = viewModel)
        return
    }

    key(signedInState.user.uid) {
        SignedInPetAppNavigation(viewModel = viewModel)
    }
}

@Composable
private fun SignedInPetAppNavigation(viewModel: PetViewModel) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val currentRoute = currentBackStackEntry?.destination?.route.orEmpty()
    val selectedDestination = when {
        currentRoute.startsWith(ROUTE_PROFILE) -> BottomNavigationDestination.Profile
        currentRoute.startsWith(ROUTE_CREATE) -> BottomNavigationDestination.Create
        currentRoute.startsWith(ROUTE_ACTIVITY) -> BottomNavigationDestination.Activity
        currentRoute.startsWith(ROUTE_NOTIFICATIONS) || currentRoute.startsWith(ROUTE_ALERT.substringBefore("{")) -> BottomNavigationDestination.Notifications
        else -> BottomNavigationDestination.Home
    }

    Scaffold(
        contentWindowInsets = WindowInsets(AppSpacing.none),
        bottomBar = {
            BottomPrimaryActionBanner(
                onHomeClick = { navController.navigateToPrimaryDestination(ROUTE_HOME) },
                onProfileClick = { navController.navigateToPrimaryDestination(ROUTE_PROFILE) },
                onCreatePostClick = { navController.navigateToCreatePost() },
                onActivityClick = { navController.navigateToPrimaryDestination(ROUTE_ACTIVITY) },
                onNotificationsClick = { navController.navigateToPrimaryDestination(ROUTE_NOTIFICATIONS) },
                unreadNotificationsCount = notifications.count { !it.isRead },
                selectedDestination = selectedDestination,
            )
        }
    ) { shellPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(shellPadding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAlert = { postId -> navController.navigate(alertRoute(postId)) },
                )
            }

            composable(ROUTE_CREATE) {
                CreatePetPostScreen(
                    viewModel = viewModel,
                    onPostCreated = { navController.popBackStack() },
                )
            }

            composable(
                route = ROUTE_ALERT,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                SightingAlertScreen(
                    viewModel = viewModel,
                    postId = postId,
                    onBackClick = { navController.popBackStack() },
                    onAlertSent = {
                        navController.navigateToPrimaryDestination(ROUTE_HOME)
                    }
                )
            }

            composable(
                route = ROUTE_SIGHTING_DETAIL,
                arguments = listOf(navArgument("sightingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sightingId = backStackEntry.arguments?.getString("sightingId").orEmpty()
                SightingDetailScreen(
                    viewModel = viewModel,
                    sightingId = sightingId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(ROUTE_ACTIVITY) {
                ActivityScreen(
                    viewModel = viewModel,
                    onSightingClick = { sightingId ->
                        resolveActivitySightingRoute(sightingId)?.let { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        } ?: Log.w(
                            ACTIVITY_TAG,
                            "Ignoring invalid Activity sighting selection id=$sightingId"
                        )
                    }
                )
            }

            composable(ROUTE_NOTIFICATIONS) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNotificationClick = { notification ->
                        resolveNotificationRoute(notification)?.let(navController::navigate)
                            ?: Log.w(
                                NOTIFICATION_TAG,
                                "Ignoring invalid sighting notification " +
                                    "id=${notification.id}, type=${notification.type}"
                            )
                    }
                )
            }

            composable(ROUTE_PROFILE) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }
}

private fun NavHostController.navigateToPrimaryDestination(route: String) {
    if (route == ROUTE_HOME && popBackStack(ROUTE_HOME, inclusive = false)) {
        return
    }
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateToCreatePost() {
    navigate(ROUTE_CREATE) {
        launchSingleTop = true
    }
}

private fun alertRoute(postId: String) = "alert/$postId"

private fun sightingDetailRoute(sightingId: String) = "sighting/$sightingId"

internal fun resolveActivitySightingRoute(sightingId: String?): String? =
    sightingId
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.let(::sightingDetailRoute)

internal fun resolveNotificationRoute(notification: AppNotificationEntity): String? =
    if (notification.type == "ALERT") {
        notification.sightingId
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let(::sightingDetailRoute)
    } else {
        null
    }
