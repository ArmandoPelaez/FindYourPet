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
      "Datos de la mascota",
      "Icons.Filled.Pets",
      "FormFieldLabel(text = \"Nombre\", required = true)",
      "FormFieldPlaceholder(\"Ej. Toby, Mia\")",
      "FormFieldLabel(\"Características\", required = false)",
      "FormFieldPlaceholder(\"Ej: color,raza,tamaño\")",
      "Mas detalles utiles para reconocerla",
      "Ubicacion",
      "Ultima ubicacion vista",
      "Publicar ficha"
    ).forEach { marker ->
      assertTrue("Missing simplified create-post marker: $marker", source.contains(marker))
    }
    assertTrue(source.contains("FormSectionTitle(text = \"Datos de la mascota\")"))
    assertTrue(source.contains("modifier = Modifier.size(AppSpacing.iconMedium)"))
    assertTrue(!source.contains("Nombre de la mascota (Ej. Toby, Mia)"))

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
    assertTrue(source.contains("characteristics = characteristics"))

    val characteristicsLabelStart = source.indexOf("FormFieldLabel(\"Características\", required = false)")
    val additionalDetailsLabelStart = source.indexOf("FormFieldLabel(\"Detalles adicionales\")")
    assertTrue(characteristicsLabelStart >= 0)
    assertTrue(characteristicsLabelStart < additionalDetailsLabelStart)
    val characteristicsField = source.substring(characteristicsLabelStart, additionalDetailsLabelStart)
    assertTrue(!characteristicsField.contains("required = true"))
    assertTrue(!characteristicsField.contains("leadingIcon"))
    assertTrue(characteristicsField.contains("FormFieldPlaceholder(\"Ej: color,raza,tamaño\")"))
    assertTrue(!characteristicsField.contains("label = { FormFieldLabel"))
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
        "enabled = lastSeenLocation.isNotBlank() && photoUri.isNotBlank() && !isSubmitting"
      )
    )
    assertTrue(source.contains("if (selectedMediaSource == null)"))
    assertTrue(source.contains("Adjunta una foto real desde camara o galeria."))
  }

  @Test
  fun missingPetName_isRejectedBeforePublicationPath() {
    val normalized = source.replace(Regex("\\s+"), " ")

    assertTrue(normalized.contains("val petNameError = requiredPetNameMessage(petName)"))
    assertTrue(normalized.contains("if (petNameError != null) { formMessage = petNameError return@AppButton }"))
    assertTrue(source.contains("if (petName.isBlank()) \"Campo obligatorio\" else null"))
    assertTrue(source.indexOf("requiredPetNameMessage(petName)") < source.indexOf("viewModel.createNewPetPost"))
    assertTrue(source.contains("formMessage = null"))
  }

  private fun createPostSource(): String {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    val root = generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
    return File(root, "app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt").readText()
  }
}
