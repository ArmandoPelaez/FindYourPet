package com.findyourpet.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared spacing and sizing tokens used by the app UI. */
object AppSpacing {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp

    val headerLogo: Dp = 36.dp
    val avatarLarge: Dp = 72.dp
    val avatarMedium: Dp = 52.dp
    val notificationAvatar: Dp = 40.dp
    val avatarIcon: Dp = 38.dp
    val iconSmall: Dp = 14.dp
    val iconMedium: Dp = 20.dp
    val iconLarge: Dp = 56.dp
    val iconExtraSmall: Dp = 12.dp
    val iconProfile: Dp = 18.dp
    val authIcon: Dp = 34.dp
    val borderWidth: Dp = 1.dp
    val cardImageMinHeight: Dp = 260.dp
    val cardImageMaxHeight: Dp = 320.dp
    const val cardImageAspectRatio: Float = 1.58f
    val mediaHeight: Dp = 220.dp
    val messageImageHeight: Dp = 120.dp
    val formFieldHeight: Dp = 120.dp
    val messageMaxWidth: Dp = 280.dp
    val authMaxWidth: Dp = 480.dp
    val expandedContentMaxWidth: Dp = 720.dp
    val contentMaxWidth: Dp = 640.dp
    val submitMaxWidth: Dp = 400.dp
    val adaptiveBreakpoint: Dp = 600.dp
    val expandedMinHeight: Dp = 520.dp
    val notesMinHeight: Dp = 100.dp
    val expandedInset: Dp = 32.dp
    val centeredInset: Dp = 24.dp
    val compactInset: Dp = 16.dp
    val imageOverlay: Dp = 14.dp
    val cardContentVertical: Dp = 18.dp
    val cardPadding: Dp = 20.dp
    val compactCardPadding: Dp = 14.dp
    val mediaOverlayPadding: Dp = 12.dp
    val compactGap: Dp = 6.dp
    val microGap: Dp = 2.dp
    val fieldGap: Dp = 12.dp
    val bottomBarInset: Dp = 12.dp
    val formGap: Dp = 14.dp
    val listGap: Dp = 8.dp
    val sectionGap: Dp = 16.dp
    val contentInset: Dp = 16.dp
    val narrowInset: Dp = 12.dp
    val textFieldInset: Dp = 4.dp
    val chatHeaderAvatar: Dp = 38.dp
    val sendIcon: Dp = 18.dp
    val submitIcon: Dp = 22.dp
    val progressIndicator: Dp = 24.dp
    val submitButtonHeight: Dp = 56.dp
    val bannerHeight: Dp = 72.dp
    val bannerHorizontalPadding: Dp = 18.dp
    val bottomNavigationIconSlotHeight: Dp = 28.dp
    val bottomNavigationIcon: Dp = 22.dp
    val bottomNavigationCreateActionSize: Dp = 52.dp
    val bottomNavigationCreateIconSize: Dp = 24.dp
    val bottomNavigationLabelGap: Dp = none
    val bottomNavigationWellSize: Dp = 68.dp
    val bottomNavigationDividerArcHeight: Dp = 8.dp
    val bottomNavigationActionLift: Dp = 8.dp
    val titleGap: Dp = 10.dp
    val actionGap: Dp = 10.dp
    val locationGap: Dp = 6.dp
    val pagerBottom: Dp = 28.dp
    val actionBottom: Dp = 88.dp
    val buttonHeight: Dp = 52.dp
}

/** Shapes that preserve the existing FindYourPet visual language. */
object AppShapes {
    val chip = RoundedCornerShape(12.dp)
    val content = RoundedCornerShape(16.dp)
    val button = RoundedCornerShape(18.dp)
    val emptyState = RoundedCornerShape(20.dp)
    val card = RoundedCornerShape(26.dp)
    val authCard = RoundedCornerShape(32.dp)
    val authHeader = RoundedCornerShape(24.dp)
    val message = RoundedCornerShape(16.dp)
    val circularInput = RoundedCornerShape(24.dp)
    val photoThumbnail = RoundedCornerShape(8.dp)
    val messageMine = RoundedCornerShape(16.dp)
    val messageOther = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 4.dp,
        bottomEnd = 16.dp,
    )
}

object AppElevation {
    val card: Dp = 6.dp
    val auth: Dp = 10.dp
    val subtle: Dp = 1.dp
    val inputBar: Dp = 4.dp
}

object AppOpacity {
    const val topBar = 0.92f
    const val subtleSurface = 0.45f
    const val banner = 0.96f
    const val bottomNavigation = 0.88f
    const val bottomNavigationSurface = 0.28f
    const val syncSurface = 0.12f
    const val unreadSurface = 0.12f
    const val mediaOverlay = 0.78f
    const val inputSurface = 0.30f
    const val border = 0.35f
    const val timestamp = 0.70f
    const val iconSurface = 0.14f
}

data class StatusColorTokens(
    val container: Color,
    val content: Color,
)

/** Semantic colors for pet status components. */
object PetStatusColors {
    @Composable
    fun forStatus(status: String): StatusColorTokens {
        return when (status.uppercase()) {
            "PERDIDO" -> StatusColorTokens(AppColors.alert, AppColors.onPrimary)
            "AVISTADO" -> StatusColorTokens(AppColors.secondaryContainer, AppColors.secondary)
            "REUNIDO" -> StatusColorTokens(AppColors.reunitedContainer, AppColors.reunited)
            else -> StatusColorTokens(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
