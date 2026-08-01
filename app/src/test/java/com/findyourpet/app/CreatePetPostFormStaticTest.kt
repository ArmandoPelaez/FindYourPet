package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatePetPostFormStaticTest {
  private val source: String = createPostSource()

  @Test
  fun simplifiedForm_showsEssentialFlowAndHidesSplitAttributeInputs() {
    listOf(
      "Toca para agregar foto",
      "Datos de la Mascota",
      "Nombre de la mascota (Ej. Toby, Mia)",
      "Mas detalles utiles para reconocerla",
      "Ubicacion",
      "Ultima ubicacion vista",
      "Publicar ficha"
    ).forEach { marker ->
      assertTrue("Missing simplified create-post marker: $marker", source.contains(marker))
    }

    listOf(
      "Fotografia principal",
      "Sin foto seleccionada",
      "Raza (Ej. Poodle)",
      "Color principal",
      "Caracteristicas distintivas",
      "var breed",
      "var color"
    ).forEach { marker ->
      assertTrue("Create-post form still exposes retired marker: $marker", !source.contains(marker))
    }

    assertTrue(source.contains("species = \"Mascota\""))
    assertTrue(source.contains("breed = \"Mestizo\""))
    assertTrue(source.contains("color = \"Variado\""))
    assertTrue(source.contains("features = recognitionDetails.ifBlank"))
  }

  @Test
  fun simplifiedForm_keepsLocationManualAndDoesNotAddCurrentLocationCapture() {
    assertTrue(source.contains("locationSource = LocationSource.MANUAL_COARSE"))
    assertTrue(source.contains("Manifest.permission.CAMERA"))

    listOf(
      "Usar ubicacion actual",
      "Ubicacion GPS capturada",
      "ACCESS_COARSE_LOCATION",
      "ACCESS_FINE_LOCATION",
      "requestCurrentLocation",
      "FusedLocationProviderClient"
    ).forEach { marker ->
      assertTrue("Create-post screen must not add current-location capture: $marker", !source.contains(marker))
    }
  }

  @Test
  fun simplifiedForm_keepsPublishDisabledUntilPhotoNameAndLocationArePresent() {
    val normalized = source.replace(Regex("\\s+"), " ")

    assertTrue(
      normalized.contains(
        "enabled = petName.isNotBlank() && lastSeenLocation.isNotBlank() && " +
          "photoUri.isNotBlank() && !isSubmitting"
      )
    )
    assertTrue(source.contains("if (selectedMediaSource == null)"))
    assertTrue(source.contains("Adjunta una foto real desde camara o galeria."))
  }

  private fun createPostSource(): String {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    val root = generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
    return File(root, "app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt").readText()
  }
}
