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
    assertTrue(source.contains(".padding(top = AppSpacing.sm)"))

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
    assertTrue(source.contains("val heroShift = minOf((AppSpacing.xl + AppSpacing.md).roundToPx(), flexibleGap)"))
    assertTrue(source.contains("val identityHeroGap = AppSpacing.compactGap.roundToPx()"))
    assertTrue(source.contains("val authenticationGap = AppSpacing.fieldGap.roundToPx()"))
    assertTrue(source.contains("// The former combined Header/Hero column had two compact gaps"))
        val normalizedSource = source.replace(Regex("\\s+"), " ")
        assertTrue(normalizedSource.contains("placeables[0].height + identityHeroGap + placeables[1].height"))
    assertTrue(source.contains("authenticationGap * 2"))
    assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(AppSpacing.compactGap)"))
    assertTrue(source.contains("placeables[0].placeRelative(0, 0)"))
    assertTrue(source.contains("placeables[1].placeRelative(0, nextY + heroShift)"))
    assertTrue(source.contains("placeables[2].placeRelative(0, nextY)"))
    assertTrue(source.contains("// IdentityHeader boundary: fixed at its existing coordinate; it receives no shift."))
    assertTrue(source.contains("// Hero boundary: only this region receives its own responsive downward shift."))
    assertTrue(source.contains("// AuthenticationBlock boundary: its measured position and subtree stay unchanged."))
    assertTrue(
      source.indexOf("// IdentityHeader boundary:") <
        source.indexOf("// Hero boundary:") &&
        source.indexOf("// Hero boundary:") <
        source.indexOf("// AuthenticationBlock boundary:")
    )
    assertFalse(source.contains("placeables.forEachIndexed"))
    assertFalse(source.contains("identityShift"))
    assertFalse(source.contains("placeables[0].placeRelative(0, heroShift)"))
    assertFalse(source.contains("Spacer(modifier = Modifier.weight(1f))"))
    assertFalse(source.contains(".heightIn(min = maxHeight)"))
    assertTrue(source.contains(".padding(horizontal = AppSpacing.lg, vertical = AppSpacing.lg)"))
    assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)"))
    assertFalse(source.contains("Arrangement.spacedBy(\n                AppSpacing.fieldGap,\n                Alignment.CenterVertically,\n            )"))
    assertTrue(source.contains(".padding(top = AppSpacing.sm)"))
    assertTrue(source.contains(".padding(bottom = AppSpacing.sm)"))
    assertTrue(source.contains(".verticalScroll(rememberScrollState())"))
    assertTrue(source.contains(".imePadding()"))
    assertFalse(source.contains("Recordarme"))
    assertFalse(source.contains("Olvidaste"))
    assertFalse(source.contains("recuperaci\u00f3n de contrase\u00f1a"))
  }

  @Test
  fun authScreen_usesApprovedDecorativeImageBehindFunctionalContent() {
    val source = authScreenSource()

    assertTrue(source.contains("painterResource(R.drawable.imagen_fondo_pantalla_login)"))
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
      .substringAfter("painterResource(R.drawable.imagen_fondo_pantalla_login)")
      .substringBefore("Column(")
    assertFalse(imageLayer.contains("clickable"))
    assertFalse(imageLayer.contains("pointer"))
    assertFalse(imageLayer.contains("focusable"))

    val image = source.indexOf("painterResource(R.drawable.imagen_fondo_pantalla_login)")
    val content = source.indexOf("verticalScroll(rememberScrollState())")
    assertTrue("The decorative image must be declared before the functional content", image < content)
  }

  @Test
  fun authScreen_usesOnlyTheTrackedApprovedBackgroundAsset() {
    val resource = File(root, "app/src/main/res/drawable-nodpi/imagen_fondo_pantalla_login.png")
    val resourceDirectory = File(root, "app/src/main/res/drawable-nodpi")

    assertTrue(resource.isFile)
    assertTrue(resourceDirectory.listFiles().orEmpty().map { it.name }.contains(resource.name))
    assertFalse(File(resourceDirectory, "imagen_fondo_pantalla_login.webp").exists())
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
  fun authScreen_exposesContextualHeaderHierarchy() {
    val source = authScreenSource()

    val identity = source.indexOf("text = \"FindYourPet\"")
    val headline = source.indexOf("text = \"Conect\\u00e1 con avisos cerca tuyo.\"")
    val supportingText = source.indexOf("text = \"Report\\u00e1, busc\\u00e1 y ayud\\u00e1 a reencontrar mascotas.\"")
    val functionalTitle = source.indexOf("text = if (isSignUp) \"Crear cuenta\" else \"Iniciar sesión\"")
    val emailField = source.indexOf("FormFieldLabel(\"Email\")")

    assertTrue(source.contains("style = MaterialTheme.typography.labelLarge"))
    assertTrue(source.contains("style = MaterialTheme.typography.headlineSmall"))
    assertTrue(source.contains("style = MaterialTheme.typography.bodyMedium"))
    assertTrue(source.contains("color = MaterialTheme.colorScheme.onSurfaceVariant"))
    assertTrue(source.contains("Row("))
    assertTrue(source.contains("// IdentityHeader boundary: this region is measured and placed independently."))
    assertTrue(source.contains("// Hero boundary: headline and supporting text form their own region."))
    assertTrue(source.contains("// AuthenticationBlock boundary: its measured position and subtree stay unchanged."))
    assertTrue(source.contains(".padding(top = AppSpacing.sm)"))
    assertTrue(source.contains(".padding(bottom = AppSpacing.sm)"))
    assertTrue(source.contains("painterResource(R.drawable.ic_launcher_foreground)"))
    assertTrue(source.contains("contentScale = ContentScale.Fit"))
    assertTrue(source.contains("modifier = Modifier.size(AppSpacing.headerLogo)"))
    assertTrue(source.contains("Spacer(modifier = Modifier.width(AppSpacing.compactGap))"))
    assertFalse(source.contains("AppSpacing.avatarLarge"))
    assertFalse(source.contains("background(MaterialTheme.colorScheme.primary.copy(alpha = AppOpacity.iconSurface), CircleShape)"))
    assertFalse(source.contains("imageVector = Icons.Outlined.AccountCircle"))
    assertTrue(identity >= 0)
    assertTrue(headline > identity)
    assertTrue(supportingText > headline)
    assertTrue(functionalTitle > supportingText)
    assertTrue(emailField > functionalTitle)
    assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(AppSpacing.fieldGap)"))
    assertFalse(source.contains("Accede para seguir avisos y ayudar a reencontrar mascotas."))
  }

  @Test
  fun authScreen_alignsHeroAndAuthenticationLabelWithoutChangingControls() {
    val source = authScreenSource()
    val hero = source.substringAfter("// Hero boundary: headline and supporting text form their own region.")
      .substringBefore("// AuthenticationBlock boundary: its measured position and subtree stay unchanged.")
    val authentication = source.substringAfter("// AuthenticationBlock boundary: its measured position and subtree stay unchanged.")

    assertTrue(hero.contains("horizontalAlignment = Alignment.Start"))
    assertTrue(hero.contains("style = MaterialTheme.typography.headlineSmall"))
    assertTrue(hero.contains("style = MaterialTheme.typography.bodyMedium"))
    assertTrue(hero.contains("textAlign = TextAlign.Start"))
    assertFalse(hero.contains("textAlign = TextAlign.Center"))
    assertTrue(authentication.contains("text = if (isSignUp)"))
    assertTrue(authentication.contains("textAlign = TextAlign.Start"))

    listOf(
      "FormFieldLabel(\"Email\")",
      "FormFieldLabel(\"Contrase\u00f1a\")",
      "AppButton(",
      "Continuar con Google",
      "Crear una cuenta",
    ).forEach { marker ->
      assertTrue("Authentication control must remain present: $marker", source.contains(marker))
    }
    assertTrue(source.contains("placeables[2].placeRelative(0, nextY)"))
    assertFalse(source.contains("placeables[2].placeRelative(0, nextY + heroShift)"))
  }

  @Test
  fun authScreen_doesNotDisplaceAuthenticationThroughASharedParent() {
    val source = authScreenSource()
    val layout = source.substringAfter("private fun LoginVerticalRegions")

    assertTrue(layout.contains("placeables[0].placeRelative(0, 0)"))
    assertTrue(layout.contains("placeables[1].placeRelative(0, nextY + heroShift)"))
    assertTrue(layout.contains("nextY += placeables[0].height + identityHeroGap"))
    assertTrue(layout.contains("nextY += placeables[1].height"))
    assertTrue(layout.contains("nextY += authenticationGap + flexibleGap"))
    assertTrue(layout.contains("placeables[2].placeRelative(0, nextY)"))
    assertTrue(layout.contains("identityHeroGap = AppSpacing.compactGap.roundToPx()"))
    assertTrue(layout.contains("authenticationGap = AppSpacing.fieldGap.roundToPx()"))
    assertFalse(layout.contains("nextY += identityShift"))
    assertFalse(layout.contains("nextY += heroShift"))
    assertFalse(layout.contains("identityShift"))
    assertFalse(layout.contains("placeables[2].placeRelative(0, nextY +"))
    assertFalse(layout.contains("padding = heroShift"))
    assertFalse(layout.contains("offset(y ="))
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

  private fun authScreenSource(): String =
    File(root, "app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt").readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
