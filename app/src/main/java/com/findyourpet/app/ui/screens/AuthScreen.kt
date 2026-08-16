package com.findyourpet.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.findyourpet.app.R
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.ui.components.AppButton
import com.findyourpet.app.ui.components.AppButtonVariant
import com.findyourpet.app.ui.components.FormFieldLabel
import com.findyourpet.app.ui.theme.AppFormTypography
import com.findyourpet.app.ui.theme.AppElevation
import com.findyourpet.app.ui.theme.AppOpacity
import com.findyourpet.app.ui.theme.AppShapes
import com.findyourpet.app.ui.theme.AppSpacing
import com.findyourpet.app.ui.viewmodel.PetViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

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
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.authHeader,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = AppOpacity.subtleSurface),
                        tonalElevation = AppElevation.subtle
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = AppSpacing.cardContentVertical, horizontal = AppSpacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.compactGap)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppSpacing.avatarLarge)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = AppOpacity.iconSurface), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(AppSpacing.authIcon)
                                )
                            }

                            Text(
                                text = if (isSignUp) "Crear cuenta" else "Iniciar sesión",
                                style = MaterialTheme.typography.headlineSmall,
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
                    }

                    if (isSignUp) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { FormFieldLabel("Nombre") },
                            textStyle = AppFormTypography.input,
                            singleLine = true,
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
                        textStyle = AppFormTypography.input,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.content
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { FormFieldLabel("Contraseña") },
                        textStyle = AppFormTypography.input,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.content
                    )

                    AppButton(
                        onClick = {
                            localMessage = null
                            if (isSignUp) {
                                viewModel.signUpWithEmail(email, password, displayName)
                            } else {
                                viewModel.signInWithEmail(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentDescription = if (isSignUp) "Crear cuenta" else "Entrar"
                    ) {
                        Icon(Icons.Filled.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(if (isSignUp) "Crear cuenta" else "Entrar")
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
                            localMessage = null
                            if (webClientId.isBlank() || webClientId == "REPLACE_WITH_WEB_CLIENT_ID") {
                                localMessage = "Configure firebase_web_client_id before Google Sign-In."
                                return@AppButton
                            }
                            scope.launch {
                                runCatching {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(webClientId)
                                        .build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()
                                    val response = CredentialManager.create(context).getCredential(context, request)
                                    GoogleIdTokenCredential.createFrom(response.credential.data).idToken
                                }.onSuccess { idToken ->
                                    viewModel.signInWithGoogleIdToken(idToken)
                                }.onFailure { error ->
                                    localMessage = when (error) {
                                        is GetCredentialCancellationException -> "Google Sign-In was cancelled."
                                        is GoogleIdTokenParsingException -> "Google credential could not be read."
                                        else -> error.message ?: "Google Sign-In failed."
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        variant = AppButtonVariant.Outlined,
                        contentDescription = "Continuar con Google"
                    ) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text("Continuar con Google")
                    }

                    TextButton(
                        onClick = {
                            isSignUp = !isSignUp
                            localMessage = null
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (isSignUp) "Ya tengo cuenta" else "Crear una cuenta")
                    }

                    val message = localMessage
                        ?: authMessage
                        ?: (authState as? AuthUiState.Unconfigured)?.message
                        ?: (authState as? AuthUiState.Error)?.message

                    if (message != null) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
            }
        }
    }
}
