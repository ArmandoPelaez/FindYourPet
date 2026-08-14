package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PrimaryNavigationShellStaticTest {
  private val root: File = repoRoot()

  @Test
  fun signedInShellOwnsBottomPrimaryActionBanner() {
    val mainActivity = mainActivitySource()
    val homeScreen = source("app/src/main/java/com/findyourpet/app/ui/screens/HomeScreen.kt")

    assertTrue(mainActivity.contains("Scaffold("))
    assertTrue(mainActivity.contains("bottomBar = {"))
    assertTrue(mainActivity.contains("BottomPrimaryActionBanner("))
    assertTrue(mainActivity.contains("modifier = Modifier.fillMaxSize()"))
    assertTrue(mainActivity.contains(".padding(shellPadding)"))
    assertTrue(!homeScreen.contains("BottomPrimaryActionBanner("))
  }

  @Test
  fun primaryBarVisibilityIsGlobalForAuthenticatedRoutes() {
    val mainActivity = mainActivitySource()

    assertTrue(mainActivity.contains("private const val ROUTE_HOME = \"home\""))
    assertTrue(mainActivity.contains("private const val ROUTE_PROFILE = \"profile\""))
    assertTrue(mainActivity.contains("private const val ROUTE_CHATS = \"chats\""))
    assertTrue(mainActivity.contains("BottomPrimaryActionBanner("))
    assertTrue(!mainActivity.contains("PRIMARY_DESTINATION_ROUTES"))
    assertTrue(!mainActivity.contains("currentRoute in"))
    assertTrue(mainActivity.contains("private const val ROUTE_CREATE = \"create\""))
    assertTrue(mainActivity.contains("private const val ROUTE_NOTIFICATIONS = \"notifications\""))
    assertTrue(mainActivity.contains("private const val ROUTE_ALERT = \"alert/{postId}\""))
    assertTrue(mainActivity.contains("private const val ROUTE_CHAT_DETAIL = \"chat/{chatId}\""))
    assertTrue(!mainActivity.contains("setOf(ROUTE_HOME, ROUTE_PROFILE, ROUTE_CHATS, ROUTE_CREATE"))
  }

  @Test
  fun primaryNavigationUsesBoundedNavigationOptions() {
    val mainActivity = mainActivitySource()

    assertTrue(mainActivity.contains("fun NavHostController.navigateToPrimaryDestination(route: String)"))
    assertTrue(mainActivity.contains("popUpTo(graph.findStartDestination().id)"))
    assertTrue(mainActivity.contains("saveState = true"))
    assertTrue(mainActivity.contains("launchSingleTop = true"))
    assertTrue(mainActivity.contains("restoreState = true"))
    assertTrue(mainActivity.contains("onHomeClick = { navController.navigateToPrimaryDestination(ROUTE_HOME) }"))
    assertTrue(mainActivity.contains("onProfileClick = { navController.navigateToPrimaryDestination(ROUTE_PROFILE) }"))
    assertTrue(mainActivity.contains("onChatClick = { navController.navigateToPrimaryDestination(ROUTE_CHATS) }"))
    assertTrue(mainActivity.contains("onNotificationsClick = { navController.navigateToPrimaryDestination(ROUTE_NOTIFICATIONS) }"))
    assertTrue(mainActivity.contains("fun NavHostController.navigateToCreatePost()"))
  }

  @Test
  fun reportActionKeepsExistingCreateDestinationAndOtherNavigationRoutesIndependent() {
    val mainActivity = mainActivitySource()
    val banner = source("app/src/main/java/com/findyourpet/app/ui/components/CommonComponents.kt")

    assertTrue(banner.contains("label = \"Reportar\""))
    assertTrue(banner.contains("contentDescription = \"Reportar\""))
    assertTrue(banner.contains("onClick = onCreatePostClick"))
    assertTrue(mainActivity.contains("onCreatePostClick = { navController.navigateToCreatePost() }"))
    assertTrue(mainActivity.contains("onHomeClick = { navController.navigateToPrimaryDestination(ROUTE_HOME) }"))
    assertTrue(mainActivity.contains("onProfileClick = { navController.navigateToPrimaryDestination(ROUTE_PROFILE) }"))
    assertTrue(mainActivity.contains("onChatClick = { navController.navigateToPrimaryDestination(ROUTE_CHATS) }"))
    assertTrue(mainActivity.contains("onNotificationsClick = { navController.navigateToPrimaryDestination(ROUTE_NOTIFICATIONS) }"))
    assertTrue(!banner.contains("label = \"Publicar\""))
  }

  @Test
  fun createPostKeepsPublishCtaInsideFormAndReportActionInNavigation() {
    val mainActivity = mainActivitySource()
    val createPost = source("app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt")

    assertTrue(mainActivity.contains("onCreatePostClick = { navController.navigateToCreatePost() }"))
    assertTrue(!mainActivity.contains("contextualCreateAction"))
    assertTrue(!mainActivity.contains("onContextualActionChanged"))
    assertTrue(createPost.contains("contentDescription = \"Publicar ficha\""))
    assertTrue(createPost.contains("Text(\"Publicar ficha\")"))
    assertTrue(!createPost.contains("BottomNavigationContextualAction"))
  }

  @Test
  fun signedInNavigationStartsFreshForAuthenticatedSession() {
    val mainActivity = mainActivitySource()

    assertTrue(mainActivity.contains("val signedInState = authState as? AuthUiState.SignedIn"))
    assertTrue(mainActivity.contains("key(signedInState.user.uid)"))
    assertTrue(mainActivity.contains("private fun SignedInPetAppNavigation(viewModel: PetViewModel)"))
    assertTrue(mainActivity.contains("startDestination = ROUTE_HOME"))

    val authGate = mainActivity
      .substringAfter("fun PetAppNavigation(viewModel: PetViewModel)")
      .substringBefore("private fun SignedInPetAppNavigation")
    assertTrue(!authGate.contains("rememberNavController()"))
  }

  @Test
  fun profileAndChatsDoNotRequirePrimaryBackArrows() {
    val mainActivity = mainActivitySource()
    val profileScreen = source("app/src/main/java/com/findyourpet/app/ui/screens/ProfileScreen.kt")
    val chatListScreen = source("app/src/main/java/com/findyourpet/app/ui/screens/ChatListScreen.kt")

    assertTrue(mainActivity.contains("ProfileScreen(viewModel = viewModel)"))
    assertTrue(mainActivity.contains("ChatListScreen("))
    assertTrue(!mainActivity.contains("ProfileScreen(\n                viewModel = viewModel,\n                onBackClick"))
    assertTrue(profileScreen.contains("onBackClick: (() -> Unit)? = null"))
    assertTrue(chatListScreen.contains("onBackClick: (() -> Unit)? = null"))
    assertTrue(profileScreen.contains("if (navigateBack != null)"))
    assertTrue(chatListScreen.contains("if (navigateBack != null)"))
  }

  @Test
  fun primaryScrollableDestinationsCanRenderBehindFixedBanner() {
    val mainActivity = mainActivitySource()
    val profileScreen = source("app/src/main/java/com/findyourpet/app/ui/screens/ProfileScreen.kt")
    val chatListScreen = source("app/src/main/java/com/findyourpet/app/ui/screens/ChatListScreen.kt")

    assertTrue(mainActivity.contains("contentWindowInsets = WindowInsets(AppSpacing.none)"))
    assertTrue(mainActivity.contains("BottomPrimaryActionBanner("))
    assertTrue(mainActivity.contains(".padding(shellPadding)"))
    assertTrue(mainActivity.contains("composable(ROUTE_PROFILE) {\n                ProfileScreen(viewModel = viewModel)\n            }"))
    assertTrue(mainActivity.contains("composable(ROUTE_CHATS) {\n                ChatListScreen("))
    assertTrue(profileScreen.contains("bottom = AppSpacing.actionBottom"))
    assertTrue(chatListScreen.contains("bottom = AppSpacing.actionBottom"))
  }

  private fun mainActivitySource() = source("app/src/main/java/com/findyourpet/app/MainActivity.kt")

  private fun source(relativePath: String) = File(root, relativePath).readText().replace("\r\n", "\n")

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
