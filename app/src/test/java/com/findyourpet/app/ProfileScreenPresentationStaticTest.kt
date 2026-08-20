package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileScreenPresentationStaticTest {
  private val source = File(repoRoot(), "app/src/main/java/com/findyourpet/app/ui/screens/ProfileScreen.kt")
    .readText()
    .replace("\r\n", "\n")

  @Test
  fun profileKeepsExistingScreenAndUsesOwnedPostsWithoutReactivation() {
    assertTrue(source.contains("fun ProfileScreen("))
    assertTrue(source.contains("text = \"Colaborador\""))
    assertTrue(source.contains("viewModel.ownedPosts"))
    assertTrue(source.contains("\\uD83D\\uDC3E Mis publicaciones"))
    assertTrue(source.contains("Marcar reunida"))
    assertTrue(source.contains("viewModel.markPetAsReunited"))
    assertTrue(source.contains("Cerrar sesi\\u00F3n"))
    assertFalse(source.contains("viewModel.allPosts"))
    assertFalse(source.contains("Mi Perfil y Colaboraci\\u00F3n"))
    assertFalse(source.contains("Email de cuenta"))
    assertFalse(source.contains("Comunidad colaborativa"))
    assertFalse(source.contains("Reabrir"))
    assertFalse(source.contains("TopAppBar"))
    assertFalse(source.contains("Text(\"Perfil\")"))
    assertTrue(source.contains("bottomNavigationSurfaceColor()"))
    assertTrue(source.contains("PetStatusChip("))
    assertTrue(source.contains("AppButtonVariant.CompactOutlined"))
    assertTrue(source.contains("Icons.Filled.Logout"))
    assertTrue(source.indexOf("bottomNavigationSurfaceColor()") < source.indexOf("Mis publicaciones"))
    assertFalse(source.contains("Ã"))
    assertFalse(source.contains("Â"))
    assertFalse(source.contains("ð"))
  }

  @Test
  fun profileRequiresConfirmationBeforeStatusMutation() {
    assertTrue(source.contains("pendingReunitedPostId"))
    assertTrue(source.contains("AlertDialog("))
    assertTrue(source.contains("publicaci\\u00F3n dejar\\u00E1 de ser visible p\\u00FAblicamente"))
    assertTrue(source.contains("Text(\"Cancelar\")"))
    assertTrue(source.contains("Text(\"Confirmar\")"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
