package com.findyourpet.app.ui.screens

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.findyourpet.app.R
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.FormFieldLabel
import com.findyourpet.app.ui.components.FormFieldPlaceholder
import com.findyourpet.app.ui.theme.AppFormTypography
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

private fun reducedMotionEnabled(context: Context): Boolean =
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    ) == 0f

private fun enterTransition(reducedMotion: Boolean): EnterTransition =
    if (reducedMotion) EnterTransition.None else fadeIn()

private fun exitTransition(reducedMotion: Boolean): ExitTransition =
    if (reducedMotion) ExitTransition.None else fadeOut()

private enum class LoginVisualState {
    Idle,
    EmailLoading,
    GoogleLoading,
    Error,
    SignedIn,
}

private fun validateEmail(value: String): String? = when {
    value.isBlank() -> "Ingres\u00e1 un email."
    !Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(value) -> "Ingres\u00e1 un email v\u00e1lido."
    else -> null
}

private fun validatePassword(value: String): String? = when {
    value.isBlank() -> "Ingres\u00e1 una contrase\u00f1a."
    value.length < 6 -> "La contrase\u00f1a debe tener al menos 6 caracteres."
    else -> null
}

@Composable
fun AuthScreen(viewModel: PetViewModel) {
    val authState by viewModel.authState.collectAsState()
    val authMessage by viewModel.authMessage.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val webClientId = stringResource(R.string.firebase_web_client_id)

    var isSignUp by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var isEmailLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var authAttempt by remember { mutableIntStateOf(0) }
    var authMessageVisible by remember { mutableStateOf(true) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }
    val reducedMotion = remember(context) { reducedMotionEnabled(context) }
    val isAuthLoading = authState is AuthUiState.Loading
    val isAuthOperationInProgress = isEmailLoading || isAuthLoading || isGoogleLoading
    val emailError = if (hasSubmitted) validateEmail(email) else null
    val passwordError = if (hasSubmitted) validatePassword(password) else null
    val hasRecoverableError = localMessage != null ||
        (authMessageVisible && (authMessage != null || authState is AuthUiState.Error || authState is AuthUiState.Unconfigured))
    val loginVisualState = when {
        authState is AuthUiState.SignedIn -> LoginVisualState.SignedIn
        isGoogleLoading -> LoginVisualState.GoogleLoading
        isEmailLoading || isAuthLoading -> LoginVisualState.EmailLoading
        hasRecoverableError -> LoginVisualState.Error
        else -> LoginVisualState.Idle
    }
    val emailBorderColor by animateColorAsState(
        targetValue = if (emailFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = if (reducedMotion) snap() else spring(),
        label = "email focus border",
    )
    val passwordBorderColor by animateColorAsState(
        targetValue = if (passwordFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = if (reducedMotion) snap() else spring(),
        label = "password focus border",
    )

    LaunchedEffect(authMessage) {
        if (authMessage != null) authMessageVisible = true
    }

    LaunchedEffect(authAttempt, isEmailLoading, isGoogleLoading) {
        if (authAttempt == 0 || (!isEmailLoading && !isGoogleLoading)) return@LaunchedEffect

        val initialAuthState = authState
        val initialAuthMessage = authMessage
        var messageWasCleared = initialAuthMessage == null

        snapshotFlow { authState to authMessage }.collect { (state, message) ->
            if (message == null) messageWasCleared = true

            val authenticationFinished = state is AuthUiState.SignedIn ||
                (state is AuthUiState.Error && state != initialAuthState) ||
                (message != null && (initialAuthMessage == null || messageWasCleared))

            if (authenticationFinished) {
                isEmailLoading = false
                isGoogleLoading = false
                authMessageVisible = true
            }
        }
    }

    fun submitEmailForm() {
        if (isAuthOperationInProgress) return
        hasSubmitted = true
        localMessage = null
        authMessageVisible = false
        if (validateEmail(email) != null || validatePassword(password) != null) return

        isEmailLoading = true
        authAttempt++
        if (isSignUp) {
            viewModel.signUpWithEmail(email, password, displayName)
        } else {
            viewModel.signInWithEmail(email, password)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.subtleSurface)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.imagen_fondo_pantalla_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.inputSurface),
                            MaterialTheme.colorScheme.surface.copy(alpha = AppOpacity.subtleSurface),
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.formGap)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppSpacing.authMaxWidth),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.formGap)
            ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = AppSpacing.cardContentVertical,
                                horizontal = AppSpacing.md,
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.compactGap)
                    ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(AppSpacing.headerLogo),
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.compactGap))
                                Text(
                                    text = "FindYourPet",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            Text(
                                text = "Conect\u00e1 con avisos cerca tuyo.",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Report\u00e1, busc\u00e1 y ayud\u00e1 a reencontrar mascotas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = if (isSignUp) "Crear cuenta" else "Iniciar sesión",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = if (isSignUp) {
                                    "Únete a FindYourPet para publicar avisos y ayudar a reencontrar mascotas."
                                } else {
                                    "Accede para seguir avisos y ayudar a reencontrar mascotas."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                    }

                    if (isSignUp) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { FormFieldLabel("Nombre") },
                            textStyle = AppFormTypography.input,
                            singleLine = true,
                            enabled = !isAuthOperationInProgress,
                            leadingIcon = {
                                Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = AppShapes.content
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { FormFieldLabel("Email") },
                        placeholder = { FormFieldPlaceholder("tu@email.com") },
                        textStyle = AppFormTypography.input,
                        singleLine = true,
                        enabled = !isAuthOperationInProgress,
                        isError = emailError != null,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emailBorderColor,
                            unfocusedBorderColor = emailBorderColor,
                        ),
                        supportingText = emailError?.let { error ->
                            { Text(error, style = AppFormTypography.placeholder, color = MaterialTheme.colorScheme.error) }
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { emailFocused = it.isFocused },
                        shape = AppShapes.content
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        enabled = !isAuthOperationInProgress,
                        isError = passwordError != null,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = passwordBorderColor,
                            unfocusedBorderColor = passwordBorderColor,
                        ),
                        supportingText = passwordError?.let { error ->
                            { Text(error, style = AppFormTypography.placeholder, color = MaterialTheme.colorScheme.error) }
                        },
                        placeholder = { FormFieldPlaceholder("Tu contrase\u00f1a") },
                        label = { FormFieldLabel("Contraseña") },
                        textStyle = AppFormTypography.input,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                enabled = !isAuthOperationInProgress
                            ) {
                                AnimatedContent(
                                    targetState = passwordVisible,
                                    transitionSpec = {
                                        if (reducedMotion) {
                                            EnterTransition.None togetherWith ExitTransition.None
                                        } else {
                                            fadeIn() togetherWith fadeOut()
                                        }
                                    },
                                    label = "password visibility affordance",
                                ) { isVisible ->
                                    Icon(
                                        imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (isVisible) {
                                            "Ocultar contrase\u00f1a"
                                        } else {
                                            "Mostrar contrase\u00f1a"
                                        },
                                        modifier = Modifier.size(AppSpacing.iconMedium)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submitEmailForm() }
                        ),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester)
                            .onFocusChanged { passwordFocused = it.isFocused }
                            .semantics { password() },
                        shape = AppShapes.content
                    )

                    AppButton(
                        onClick = { submitEmailForm() },
                        enabled = !isAuthOperationInProgress,
                        modifier = Modifier.fillMaxWidth(),
                        contentDescription = if (isSignUp) "Crear cuenta" else "Entrar"
                    ) {
                        if (loginVisualState == LoginVisualState.EmailLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(AppSpacing.iconMedium)
                            )
                        } else {
                            Icon(Icons.Filled.Login, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(
                            when {
                                loginVisualState == LoginVisualState.EmailLoading && isSignUp -> "Creando cuenta..."
                                loginVisualState == LoginVisualState.EmailLoading -> "Ingresando..."
                                isSignUp -> "Crear cuenta"
                                else -> "Entrar"
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "o",
                            modifier = Modifier.padding(horizontal = AppSpacing.sm),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    AppButton(
                        onClick = {
                            if (isAuthOperationInProgress) return@AppButton
                            localMessage = null
                            authMessageVisible = false
                            if (webClientId.isBlank() || webClientId == "REPLACE_WITH_WEB_CLIENT_ID") {
                                localMessage = "Configure firebase_web_client_id before Google Sign-In."
                                return@AppButton
                            }
                            isGoogleLoading = true
                            authAttempt++
                            scope.launch {
                                try {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(webClientId)
                                        .build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()
                                    val response = CredentialManager.create(context).getCredential(context, request)
                                    val idToken = GoogleIdTokenCredential.createFrom(response.credential.data).idToken
                                    viewModel.signInWithGoogleIdToken(idToken)
                                } catch (error: Exception) {
                                    localMessage = when (error) {
                                        is GetCredentialCancellationException -> "Google Sign-In was cancelled."
                                        is GoogleIdTokenParsingException -> "Google credential could not be read."
                                        else -> error.message ?: "Google Sign-In failed."
                                    }
                                    isGoogleLoading = false
                                } finally {
                                    if (localMessage != null) isGoogleLoading = false
                                }
                            }
                        },
                        enabled = !isAuthOperationInProgress,
                        modifier = Modifier.fillMaxWidth(),
                        variant = AppButtonVariant.Outlined,
                        contentDescription = "Continuar con Google"
                    ) {
                        if (loginVisualState == LoginVisualState.GoogleLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(AppSpacing.iconMedium)
                            )
                        } else {
                            // Official standard-color G asset from the Google Play Services resource bundle:
                            // https://developers.google.com/identity/branding-guidelines
                            // Keep its intrinsic ratio and colors; FindYourPet styles only the outer button and text.
                            Image(
                                painter = painterResource(R.drawable.google_sign_in_g_standard_color),
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                         Text(if (loginVisualState == LoginVisualState.GoogleLoading) "Conectando..." else "Continuar con Google")
                    }

                    TextButton(
                        onClick = {
                            if (isAuthOperationInProgress) return@TextButton
                            isSignUp = !isSignUp
                            localMessage = null
                            authMessageVisible = false
                        },
                        enabled = !isAuthOperationInProgress,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .semantics {
                                contentDescription = if (isSignUp) {
                                    "Ya tengo cuenta"
                                } else {
                                    "Crear una cuenta"
                                }
                            }
                    ) {
                        Text(if (isSignUp) "Ya tengo cuenta" else "Crear una cuenta")
                    }

                    val message = localMessage
                        ?: if (authMessageVisible) authMessage else null
                        ?: if (authMessageVisible) (authState as? AuthUiState.Unconfigured)?.message else null
                        ?: if (authMessageVisible) (authState as? AuthUiState.Error)?.message else null

                    AnimatedVisibility(
                        visible = message != null,
                        enter = enterTransition(reducedMotion),
                        exit = exitTransition(reducedMotion),
                    ) {
                        Text(
                            text = message.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    AnimatedVisibility(
                        visible = loginVisualState == LoginVisualState.SignedIn,
                        enter = enterTransition(reducedMotion),
                        exit = exitTransition(reducedMotion),
                    ) {
                        Text(
                            text = "Autenticación exitosa.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
            }
        }
    }
}
