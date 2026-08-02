package com.findyourpet.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.ui.theme.AlertRed
import com.findyourpet.app.ui.theme.AlertRedContainer
import com.findyourpet.app.ui.theme.CoralPrimary
import com.findyourpet.app.ui.theme.ReunitedGreen
import com.findyourpet.app.ui.theme.ReunitedGreenContainer
import com.findyourpet.app.ui.theme.TealSecondary
import com.findyourpet.app.ui.theme.TealSecondaryContainer

@Composable
fun PetStatusChip(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label, icon) = when (status.uppercase()) {
        "PERDIDO" -> Quadruple(AlertRedContainer, AlertRed, "PERDIDO", Icons.Filled.Warning)
        "AVISTADO" -> Quadruple(TealSecondaryContainer, TealSecondary, "AVISTADO", Icons.Filled.Visibility)
        "REUNIDO" -> Quadruple(ReunitedGreenContainer, ReunitedGreen, "REUNIDO", Icons.Filled.CheckCircle)
        else -> Quadruple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status, Icons.Filled.Info)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun BottomPrimaryActionBanner(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .navigationBarsPadding()
            .semantics { contentDescription = "Acciones principales" },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f),
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }

            FilledIconButton(
                onClick = onCreatePostClick,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = CoralPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear publicacion",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onChatClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = "Chats Privados",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun <T> SyncStatusBanner(
    state: BackendSyncState<T>,
    modifier: Modifier = Modifier
) {
    val message = when {
        state.errorMessage != null -> state.errorMessage
        state.isLoading -> "Cargando datos"
        state.hasPendingWrites -> "Guardando cambios"
        state.isFromCache && state.isRemoteBackend -> "Mostrando datos en cache"
        !state.isRemoteBackend -> "Modo local"
        else -> null
    } ?: return

    val color = when {
        state.errorMessage != null -> AlertRed
        state.hasPendingWrites -> CoralPrimary
        state.isLoading -> TealSecondary
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        contentColor = color,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (state.errorMessage != null) Icons.Filled.Error else Icons.Filled.Sync,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

