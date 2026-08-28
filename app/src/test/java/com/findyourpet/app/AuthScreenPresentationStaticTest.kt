package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScreenPresentationStaticTest {
  private val root: File = repoRoot()

  @Test
  fun authScreen_usesFullViewportScrollableCompositionAndKeepsAuthActions() {
    val source = authScreenSource()

    assertFalse(source.contains("import androidx.compose.material3.Card"))
    assertFalse(source.contains("Card("))
    assertTrue(source.contains(".windowInsetsPadding(WindowInsets.safeDrawing)"))
    assertTrue(source.contains(".imePadding()"))
    assertTrue(source.contains(".verticalScroll(rememberScrollState())"))
    assertTrue(source.contains(".widthIn(max = AppSpacing.authMaxWidth)"))
    assertFalse(source.contains("Surface("))
    assertFalse(source.contains("AppShapes.authHeader"))
    assertFalse(source.contains("AppElevation.subtle"))
    assertTrue(source.contains("AppSpacing.fieldGap"))
  }

  @Test
  fun authScreen_usesOneContainedColumnAndExplicitActionHierarchy() {
    val source = authScreenSource()

    assertTrue(source.contains(".widthIn(max = AppSpacing.authMaxWidth)"))
    assertTrue(source.contains(".padding(horizontal = AppSpacing.md),"))

    val primaryActionPattern = Regex(
      """AppButton\(\s*onClick = \{\s*submitEmailForm\(\)\s*\}.*?variant = AppButtonVariant\.Primary.*?contentDescription = if \(isSignUp\) \"Crear cuenta\" else \"Entrar\"""",
      setOf(RegexOption.DOT_MATCHES_ALL),
    )
    assertTrue(primaryActionPattern.containsMatchIn(source))

    assertTrue(source.contains("variant = AppButtonVariant.Outlined"))
    assertTrue(source.contains("TextButton("))
    assertFalse(source.contains("max = 480.dp"))
    assertFalse(Regex("(width|widthIn|padding|size|height|spacedBy)\\([^)]*\\b\\d+(\\.\\d+)?\\.dp").containsMatchIn(source))
  }

  @Test
  fun authScreen_usesResponsiveCenteredVerticalRhythmWithoutReferenceOnlyControls() {
    val source = authScreenSource()

    assertTrue(source.contains("BoxWithConstraints("))
    assertTrue(source.contains("viewportHeight = contentViewportHeight"))
    assertTrue(source.contains("private fun LoginVerticalRegions"))
    assertTrue(source.contains("val flexibleGap = flexibleSpace / 2"))
    assertTrue(source.contains("val authenticationGap = AppSpacing.fieldGap.roundToPx()"))
    assertTrue(source.contains("val naturalHeight = authentication.height + authenticationGap * 2"))
    assertTrue(source.contains("val authenticationShift = AppSpacing.authBlockVerticalShift.roundToPx()"))
    assertTrue(source.contains("val authenticationTop = authenticationGap + flexibleGap + authenticationShift"))
    assertTrue(source.contains("authentication.placeRelative(0, authenticationTop)"))
    assertFalse(source.contains("heroShift"))
    assertFalse(source.contains("placeables[0]"))
    assertFalse(source.contains("placeables[1]"))
    assertFalse(source.contains("placeables[2]"))
    assertFalse(source.contains("Spacer(modifier = Modifier.weight(1f))"))
    assertFalse(source.contains(".heightIn(min = maxHeight)"))
    assertTrue(source.contains(".padding(horizontal = AppSpacing.lg, vertical = AppSpacing.lg)"))
    assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)"))
    assertFalse(source.contains("Arrangement.spacedBy(\n                AppSpacing.fieldGap,\n                Alignment.CenterVertically,\n            )"))
    assertTrue(source.contains(".verticalScroll(rememberScrollState())"))
    assertTrue(source.contains(".imePadding()"))
    assertFalse(source.contains("Recordarme"))
    assertFalse(source.contains("Olvidaste"))
    assertFalse(source.contains("recuperaci\u00f3n de contrase\u00f1a"))
  }

  @Test
  fun authScreen_usesApprovedDecorativeImageBehindFunctionalContent() {
    val source = authScreenSource()

    assertTrue(source.contains("painterResource(R.drawable.imagen_nuevo_fondo_pantalla)"))
    assertTrue(source.contains("contentDescription = null"))
    assertTrue(source.contains("modifier = Modifier.fillMaxSize()"))
    assertTrue(source.contains("contentScale = ContentScale.Crop"))
    assertTrue(source.contains("alignment = Alignment.TopCenter"))
    assertTrue(source.contains("AppOpacity.inputSurface"))
    assertFalse(source.contains("LoginProximityBackground"))
    assertFalse(source.contains("Canvas("))

    listOf(
      "FormFieldLabel(\"Email\")",
      "FormFieldLabel(\"Contraseña\")",
      "contentDescription = if (isSignUp) \"Crear cuenta\" else \"Entrar\"",
      "contentDescription = \"Continuar con Google\"",
      "authMessageVisible",
    ).forEach { marker ->
      assertTrue("Login content must remain available above the decorative layer: $marker", source.contains(marker))
    }

    val imageLayer = source
      .substringAfter("painterResource(R.drawable.imagen_nuevo_fondo_pantalla)")
      .substringBefore("Column(")
    assertFalse(imageLayer.contains("clickable"))
    assertFalse(imageLayer.contains("pointer"))
    assertFalse(imageLayer.contains("focusable"))

    val image = source.indexOf("painterResource(R.drawable.imagen_nuevo_fondo_pantalla)")
    val content = source.indexOf("verticalScroll(rememberScrollState())")
    assertTrue("The decorative image must be declared before the functional content", image < content)
  }

  @Test
  fun authScreen_usesOnlyTheTrackedApprovedBackgroundAsset() {
    val resource = File(root, "app/src/main/res/drawable-nodpi/imagen_nuevo_fondo_pantalla.png")
    val resourceDirectory = File(root, "app/src/main/res/drawable-nodpi")

    assertTrue(resource.isFile)
    assertTrue(resourceDirectory.listFiles().orEmpty().map { it.name }.contains(resource.name))
    assertFalse(File(resourceDirectory, "imagen_nuevo_fondo_pantalla.webp").exists())
  }

  @Test
  fun authScreen_preservesAuthenticationCallbacksAndThemeAwarePresentation() {
    val source = authScreenSource()

    listOf(
      "viewModel.signInWithEmail(email, password)",
      "viewModel.signUpWithEmail(email, password, displayName)",
      "viewModel.signInWithGoogleIdToken(idToken)",
      "contentDescription = \"Continuar con Google\"",
      "GetCredentialCancellationException",
      "firebase_web_client_id",
      "MaterialTheme.colorScheme",
      "AppFormTypography.input",
      "AppShapes.content",
      "AppButton"
    ).forEach { marker ->
      assertTrue("AuthScreen must preserve: $marker", source.contains(marker))
    }

    assertFalse(source.contains("Color("))
    assertFalse(Regex("\\b\\d+\\.sp\\b").containsMatchIn(source))
  }

  @Test
  fun authScreen_removesRedundantBackgroundCopyAndKeepsAuthenticationBlock() {
    val source = authScreenSource()

    val functionalTitle = source.indexOf("text = if (isSignUp) \"Crear cuenta\" else \"Iniciar sesión\"")
    val emailField = source.indexOf("FormFieldLabel(\"Email\")")

    assertFalse(source.contains("Únete a FindYourPet para publicar avisos y ayudar a reencontrar mascotas."))
    assertFalse(source.contains("Ãšnete a FindYourPet para publicar avisos y ayudar a reencontrar mascotas."))
    assertFalse(source.contains("text = \"FindYourPet\""))
    assertFalse(source.contains("Conect\\u00e1 con avisos cerca tuyo."))
    assertFalse(source.contains("Report\\u00e1, busc\\u00e1 y ayud\\u00e1 a reencontrar mascotas."))
    assertFalse(source.contains("painterResource(R.drawable.ic_launcher_foreground)"))
    assertFalse(source.contains("AppSpacing.avatarLarge"))
    assertFalse(source.contains("background(MaterialTheme.colorScheme.primary.copy(alpha = AppOpacity.iconSurface), CircleShape)"))
    assertFalse(source.contains("imageVector = Icons.Outlined.AccountCircle"))
    assertTrue(functionalTitle >= 0)
    assertTrue(emailField > functionalTitle)
    assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)"))
    assertFalse(source.contains("Accede para seguir avisos y ayudar a reencontrar mascotas."))
  }

  @Test
  fun authScreen_keepsAuthenticationControlsAfterRemovingRedundantCopy() {
    val source = authScreenSource()
    listOf(
      "FormFieldLabel(\"Email\")",
      "FormFieldLabel(\"Contrase\u00f1a\")",
      "AppButton(",
      "Continuar con Google",
      "Crear una cuenta",
    ).forEach { marker ->
      assertTrue("Authentication control must remain present: $marker", source.contains(marker))
    }
    assertTrue(source.contains("authentication.placeRelative(0, authenticationTop)"))
    assertFalse(source.contains("heroShift"))
  }

  @Test
  fun authScreen_doesNotDisplaceAuthenticationThroughASharedParent() {
    val source = authScreenSource()
    val layout = source.substringAfter("private fun LoginVerticalRegions")

    assertTrue(layout.contains("val authentication = measurables.single().measure"))
    assertTrue(layout.contains("val naturalHeight = authentication.height + authenticationGap * 2"))
    assertTrue(layout.contains("authenticationGap = AppSpacing.fieldGap.roundToPx()"))
    assertTrue(layout.contains("val authenticationShift = AppSpacing.authBlockVerticalShift.roundToPx()"))
    assertTrue(layout.contains("authentication.placeRelative(0, authenticationTop)"))
    assertFalse(layout.contains("placeables"))
    assertFalse(layout.contains("heroShift"))
    assertFalse(layout.contains("Spacer(modifier = Modifier.weight(1f))"))
    assertFalse(layout.contains("heightIn(min = maxHeight)"))
    assertFalse(Regex("(padding|offset|height|size)\\([^)]*\\b\\d+(\\.\\d+)?\\.dp").containsMatchIn(source))
  }

  @Test
  fun authScreen_exposesIntegratedFieldStatesAndInputSemantics() {
    val source = authScreenSource()

    listOf(
      "FormFieldPlaceholder(\"tu@email.com\")",
      "FormFieldPlaceholder(\"Tu contrase",
      "Icons.Filled.Visibility",
      "Icons.Filled.VisibilityOff",
      "passwordVisible",
      "contentDescription = if (isVisible)",
      "\"Ocultar contrase\\u00f1a\"",
      "\"Mostrar contrase\\u00f1a\"",
      "KeyboardType.Email",
      "KeyboardType.Password",
      "ImeAction.Next",
      "ImeAction.Done",
      "passwordFocusRequester.requestFocus()",
      "onDone = { submitEmailForm() }",
      ".imePadding()",
      "isError = emailError != null",
      "isError = passwordError != null",
      "supportingText = emailError?.let",
      "supportingText = passwordError?.let",
      "enabled = !isAuthOperationInProgress",
      ".semantics { password() }",
      "validateEmail(email)",
      "validatePassword(password)"
    ).forEach { marker ->
      assertTrue("AuthScreen must expose: $marker", source.contains(marker))
    }

    assertFalse(source.contains("ExperimentalComposeUiApi"))
    assertFalse(source.contains("AutofillType"))
    assertTrue(source.contains("keyboardType = KeyboardType.Email"))
    assertTrue(source.contains("keyboardType = KeyboardType.Password"))
    assertTrue(source.contains(".semantics { password() }"))
    assertFalse(source.contains(".then(if (!passwordVisible) Modifier.semantics { password() } else Modifier)"))

    val visibilityDescription = source
      .substringAfter("contentDescription = if (isVisible)")
      .substringBefore("modifier = Modifier.size(AppSpacing.iconMedium)")
    assertTrue(
      visibilityDescription.indexOf("\"Ocultar contrase\\u00f1a\"") <
        visibilityDescription.indexOf("\"Mostrar contrase\\u00f1a\"")
    )
  }

  @Test
  fun authScreen_exposesActionHierarchyAndConcurrentSubmitProtection() {
    val source = authScreenSource()

    listOf(
      "var isGoogleLoading by remember { mutableStateOf(false) }",
      "val isAuthOperationInProgress = isEmailLoading || isAuthLoading || isGoogleLoading",
      "if (isAuthOperationInProgress) return",
      "enabled = !isAuthOperationInProgress",
      "variant = AppButtonVariant.Outlined",
      "contentDescription = \"Continuar con Google\"",
      "Icons.Outlined.AccountCircle",
      "CircularProgressIndicator",
      "isGoogleLoading = true",
      "finally",
      "isGoogleLoading = false",
      "TextButton(",
      "contentDescription = if (isSignUp)"
    ).forEach { marker ->
      assertTrue("AuthScreen must expose action state: $marker", source.contains(marker))
    }

    assertFalse(source.contains("Color("))
    assertFalse(Regex("\\b\\d+\\.sp\\b").containsMatchIn(source))
  }

  @Test
  fun authScreen_usesOfficialGoogleBrandAssetAndRejectsGenericAccountIcon() {
    val source = authScreenSource()
    val googleAction = source
      .substringAfter("contentDescription = \"Continuar con Google\"")
      .substringBefore("TextButton(")

    assertTrue(source.contains("painterResource(R.drawable.google_sign_in_g_standard_color)"))
    assertTrue(source.contains("Official standard-color G asset from the Google Play Services resource bundle"))
    assertFalse(source.contains("google_sign_in_light_square"))
    assertFalse(source.contains("google_sign_in_dark_square"))
    assertTrue(source.contains("https://developers.google.com/identity/branding-guidelines"))
    assertTrue(googleAction.contains("Image("))
    assertFalse(googleAction.contains("Icons.Outlined.AccountCircle"))
    assertFalse(googleAction.contains("Color("))
    assertFalse(googleAction.contains("alpha ="))
  }

  @Test
  fun authScreen_exposesFiniteFocusVisibilityAndReducedMotionTransitions() {
    val source = authScreenSource()

    listOf(
      "animateColorAsState",
      "onFocusChanged",
      "OutlinedTextFieldDefaults.colors",
      "AnimatedContent(",
      "label = \"password visibility affordance\"",
      "AnimatedVisibility(",
      "EnterTransition.None",
      "ExitTransition.None",
      "Settings.Global.ANIMATOR_DURATION_SCALE",
      "reducedMotionEnabled(context)",
      "private enum class LoginVisualState",
      "LoginVisualState.Idle",
      "LoginVisualState.EmailLoading",
      "LoginVisualState.GoogleLoading",
      "LoginVisualState.Error",
      "LoginVisualState.SignedIn",
      "fadeIn()",
      "fadeOut()",
    ).forEach { marker ->
      assertTrue("AuthScreen must expose visual-state feedback: $marker", source.contains(marker))
    }

    assertFalse(source.contains("infiniteRepeatable"))
    assertFalse(source.contains("while (true)"))
  }

  @Test
  fun authScreen_sharesOperationGuardAndResetsRecoverably() {
    val source = authScreenSource()

    listOf(
      "var isEmailLoading by remember { mutableStateOf(false) }",
      "var isGoogleLoading by remember { mutableStateOf(false) }",
      "val isAuthOperationInProgress = isEmailLoading || isAuthLoading || isGoogleLoading",
      "if (isAuthOperationInProgress) return",
      "isEmailLoading = true",
      "isGoogleLoading = true",
      "authAttempt++",
      "snapshotFlow { authState to authMessage }",
      "isEmailLoading = false",
      "isGoogleLoading = false",
      "authMessageVisible = false",
      "enabled = !isAuthOperationInProgress",
    ).forEach { marker ->
      assertTrue("AuthScreen must expose operation recovery: $marker", source.contains(marker))
    }
  }

  @Test
  fun authScreen_observesSuccessWithoutBlockingAuthenticatedNavigation() {
    val source = authScreenSource()

    assertTrue(source.contains("visible = loginVisualState == LoginVisualState.SignedIn"))
    assertTrue(source.contains("Autenticaci\u00f3n exitosa."))
    assertFalse(source.contains("delay("))
    assertFalse(source.contains("Thread.sleep"))
    assertTrue(source.contains("viewModel.signInWithEmail(email, password)"))
    assertTrue(source.contains("viewModel.signInWithGoogleIdToken(idToken)"))
  }

  @Test
  fun authScreen_clearsLoginCredentials_onlyForGoogleFallback_andKeepsMessageVisible() {
    val source = authScreenSource()

    assertTrue(source.contains("shouldClearEmailPasswordAfterFallback("))
    assertTrue(source.contains("isSignUp = isSignUp"))
    assertTrue(source.contains("isEmailAttempt = isEmailLoading"))
    assertTrue(source.contains("email = \"\""))
    assertTrue(source.contains("password = \"\""))
    assertTrue(source.contains("hasSubmitted = false"))
    assertTrue(source.contains("authMessageVisible = true"))
    assertTrue(source.indexOf("password = \"\"") < source.lastIndexOf("authMessageVisible = true"))
  }

  private fun authScreenSource(): String =
    File(root, "app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt").readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
