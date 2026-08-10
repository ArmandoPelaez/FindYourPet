package com.findyourpet.app

import android.os.Bundle
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.ui.components.BottomPrimaryActionBanner
import com.findyourpet.app.ui.screens.AuthScreen
import com.findyourpet.app.ui.screens.ChatDetailScreen
import com.findyourpet.app.ui.screens.ChatListScreen
import com.findyourpet.app.ui.screens.CreatePetPostScreen
import com.findyourpet.app.ui.screens.HomeScreen
import com.findyourpet.app.ui.screens.NotificationsScreen
import com.findyourpet.app.ui.screens.ProfileScreen
import com.findyourpet.app.ui.screens.SightingAlertScreen
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
private const val ROUTE_CHATS = "chats"
private const val ROUTE_NOTIFICATIONS = "notifications"
private const val ROUTE_ALERT = "alert/{postId}"
private const val ROUTE_CHAT_DETAIL = "chat/{chatId}"

private val PRIMARY_DESTINATION_ROUTES = setOf(ROUTE_HOME, ROUTE_PROFILE, ROUTE_CHATS)

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
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(AppSpacing.none),
        bottomBar = {
            if (currentRoute in PRIMARY_DESTINATION_ROUTES) {
                BottomPrimaryActionBanner(
                    onHomeClick = { navController.navigateToPrimaryDestination(ROUTE_HOME) },
                    onProfileClick = { navController.navigateToPrimaryDestination(ROUTE_PROFILE) },
                    onCreatePostClick = { navController.navigateToCreatePost() },
                    onChatClick = { navController.navigateToPrimaryDestination(ROUTE_CHATS) },
                    modifier = Modifier.padding(bottom = AppSpacing.bottomBarInset)
                )
            }
        }
    ) { shellPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(shellPadding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAlert = { postId -> navController.navigate(alertRoute(postId)) },
                    onNavigateToNotifications = { navController.navigate(ROUTE_NOTIFICATIONS) }
                )
            }

            composable(ROUTE_CREATE) {
                CreatePetPostScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPostCreated = { navController.popBackStack() }
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
                route = ROUTE_CHAT_DETAIL,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                ChatDetailScreen(
                    viewModel = viewModel,
                    chatId = chatId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(ROUTE_CHATS) {
                ChatListScreen(
                    viewModel = viewModel,
                    onChatSelect = { chatId -> navController.navigate(chatDetailRoute(chatId)) }
                )
            }

            composable(ROUTE_NOTIFICATIONS) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNotificationClick = { targetId ->
                        navController.navigate(chatDetailRoute(targetId))
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

private fun chatDetailRoute(chatId: String) = "chat/$chatId"
