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
    assertTrue(source.contains("Surface("))
    assertTrue(source.contains("AppSpacing.formGap"))
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

    assertTrue(source.contains("style = MaterialTheme.typography.labelLarge"))
    assertTrue(source.contains("style = MaterialTheme.typography.headlineSmall"))
    assertTrue(source.contains("style = MaterialTheme.typography.bodyMedium"))
    assertTrue(source.contains("color = MaterialTheme.colorScheme.onSurfaceVariant"))
    assertTrue(identity >= 0)
    assertTrue(headline > identity)
    assertTrue(supportingText > headline)
    assertTrue(functionalTitle > supportingText)
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
      "contentDescription = if (passwordVisible)",
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
      .substringAfter("contentDescription = if (passwordVisible)")
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
      "val isAuthOperationInProgress = isAuthLoading || isGoogleLoading",
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

  private fun authScreenSource(): String =
    File(root, "app/src/main/java/com/findyourpet/app/ui/screens/AuthScreen.kt").readText()

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
