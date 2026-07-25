package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CreatePetPostScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PetDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SightingAlertScreen
import com.example.ui.theme.MascotasPerdidasTheme
import com.example.ui.viewmodel.PetViewModel

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

@Composable
fun PetAppNavigation(viewModel: PetViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = { postId -> navController.navigate("detail/$postId") },
                onNavigateToCreate = { navController.navigate("create") },
                onNavigateToAlert = { postId -> navController.navigate("alert/$postId") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToChatList = { navController.navigate("chats") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable(
            route = "detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PetDetailScreen(
                viewModel = viewModel,
                postId = postId,
                onBackClick = { navController.popBackStack() },
                onSendAlertClick = { navController.navigate("alert/$postId") },
                onStartChatClick = { chatId -> navController.navigate("chat/$chatId") }
            )
        }

        composable("create") {
            CreatePetPostScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }

        composable(
            route = "alert/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            SightingAlertScreen(
                viewModel = viewModel,
                postId = postId,
                onBackClick = { navController.popBackStack() },
                onAlertSent = { chatId ->
                    navController.popBackStack()
                    navController.navigate("chat/$chatId")
                }
            )
        }

        composable(
            route = "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(
                viewModel = viewModel,
                chatId = chatId,
                onBackClick = { navController.popBackStack() },
                onViewPetDetailClick = { postId -> navController.navigate("detail/$postId") }
            )
        }

        composable("chats") {
            ChatListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onChatSelect = { chatId -> navController.navigate("chat/$chatId") }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { targetId ->
                    navController.navigate("chat/$targetId")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToDetail = { postId -> navController.navigate("detail/$postId") }
            )
        }
    }
}
