package com.findyourpet.app

import java.io.File
import com.findyourpet.app.ui.screens.AdditionalDetailsMaxLength
import com.findyourpet.app.ui.screens.limitAdditionalDetailsInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatePetPostFormStaticTest {
  private val source: String = createPostSource()

  @Test
  fun createPostHeader_isIntegratedAndUsesExistingTokens() {
    assertTrue(source.contains("contentWindowInsets = WindowInsets.safeDrawing"))
    assertTrue(source.contains("text = \"Publicar mascota perdida\""))
    assertTrue(source.contains("style = MaterialTheme.typography.titleLarge"))
    assertTrue(source.contains("FormPhotoUploadSurface("))
    assertTrue(source.indexOf("text = \"Publicar mascota perdida\"") < source.indexOf("FormPhotoUploadSurface("))
    assertTrue(!source.contains("TopAppBar("))
    assertTrue(!source.contains("Icons.Filled.ArrowBack"))
    assertTrue(!source.contains("onBackClick"))
  }

  @Test
  fun simplifiedForm_showsEssentialFlowAndHidesSplitAttributeInputs() {
    listOf(
      "Toca para agregar foto", "Datos de la mascota", "Icons.Filled.Pets",
      "FormFieldLabel(text = \"Nombre\", required = true)", "FormFieldPlaceholder(\"Ej. Toby, Mia\")",
      "Descripcion adicional", "Contanos cómo reconocerla...", "¿Dónde fue vista por última vez?",
      "Seleccionar ubicación", "Usar mi ubicación actual", "Elegir en el mapa", "Escribir una referencia",
      "Publicar ficha"
    ).forEach { marker -> assertTrue("Missing simplified create-post marker: $marker", source.contains(marker)) }
    assertTrue(source.contains("FormSectionTitle(text = \"Datos de la mascota\")"))
    assertTrue(source.contains("modifier = Modifier.size(AppSpacing.iconMedium)"))
    assertTrue(!source.contains("Nombre de la mascota (Ej. Toby, Mia)"))

    listOf("Fotografia principal", "Sin foto seleccionada", "Raza (Ej. Poodle)", "Color principal",
      "Caracteristicas distintivas", "var breed", "var color").forEach { marker ->
      assertTrue("Create-post form still exposes retired marker: $marker", !source.contains(marker))
    }
    assertTrue(source.contains("species = \"Mascota\""))
    assertTrue(source.contains("breed = \"Mestizo\""))
    assertTrue(source.contains("color = \"Variado\""))
    assertTrue(source.contains("features = recognitionDetails.ifBlank"))
    val additionalDetailsLabelStart = source.indexOf("FormFieldLabel(\"Descripcion adicional\")")
    assertTrue(additionalDetailsLabelStart >= 0)
    assertTrue(!source.contains("Características"))
    assertTrue(!source.contains("Señas particulares"))
    assertTrue(!source.contains("var characteristics"))
    assertTrue(!source.contains("var particularMarks"))
    assertTrue(!source.contains("characteristics = characteristics"))
    assertTrue(!source.contains("particularMarks = particularMarks"))
  }

  @Test
  fun additionalDetails_usesMultilineInputWithCounterAndLimit() {
    assertTrue(source.contains("onValueChange = { recognitionDetails = limitAdditionalDetailsInput(it) }"))
    assertTrue(source.contains("placeholder = { FormFieldPlaceholder(\"Contanos cómo reconocerla...\") }"))
    assertTrue(source.contains("text = \"\${recognitionDetails.length}/\$AdditionalDetailsMaxLength\""))
    assertTrue(source.contains("minLines = 3"))
    assertTrue(source.contains("maxLines = 4"))
    assertTrue(source.contains("AppFormTypography.placeholder"))
    assertTrue(source.contains("AppSpacing.formFieldHeight"))
  }

  @Test
  fun additionalDetails_inputIsCappedAtFiveHundredCharacters() {
    assertEquals(500, AdditionalDetailsMaxLength)
    assertEquals(500, limitAdditionalDetailsInput("x".repeat(501)).length)
    assertEquals("texto", limitAdditionalDetailsInput("texto"))
  }

  @Test
  fun guidedLocation_usesGeocoderWithoutAutomaticManualFallback() {
    assertTrue(source.contains("mutableStateOf(LocationSource.NONE)"))
    assertTrue(source.contains("Manifest.permission.CAMERA"))
    assertTrue(source.contains("DeviceLocationProvider.currentLocation(context)"))
    assertTrue(source.contains("reverseGeocode(context, latitude, longitude)"))
    assertTrue(source.contains("selection.copy(displayText = it)"))
    assertTrue(source.contains("ACCESS_COARSE_LOCATION"))
    assertTrue(source.contains("ACCESS_FINE_LOCATION"))
    assertTrue(source.contains("LocationSelection.manualReference"))
    assertTrue(source.contains("MapLocationSheet"))
    assertTrue(source.contains("Punto seleccionado en el mapa"))
    assertTrue(!source.contains("No encontramos una dirección"))
    assertTrue(!source.contains("PlacesSearchSheet"))
    assertTrue(!source.contains("Buscar direcciÃ³n"))
    assertTrue(!source.contains("GooglePlaces"))
  }

  @Test
  fun simplifiedForm_keepsPublishDisabledUntilPhotoNameAndLocationArePresent() {
    val normalized = source.replace(Regex("\\s+"), " ")
    assertTrue(normalized.contains("val canSubmit = locationSelection?.isValid == true && photoUri.isNotBlank() && petName.isNotBlank() && !isSubmitting"))
    assertTrue(normalized.contains("enabled = canSubmit"))
    assertTrue(source.contains("contentDescription = \"Publicar ficha\""))
    assertTrue(source.contains("Text(\"Publicar ficha\")"))
    assertTrue(!source.contains("onContextualActionChanged"))
    assertTrue(!source.contains("BottomNavigationContextualAction"))
    assertTrue(source.contains("if (selectedMediaSource == null)"))
    assertTrue(source.contains("Adjunta una foto real desde camara o galeria."))
    assertTrue(source.contains("if (locationSelection?.isValid != true)"))
    assertTrue(source.contains("if (isSubmitting) return"))
    assertTrue(source.contains("isSubmitting = true"))
    assertTrue(source.contains("if (isSubmitting)"))
  }

  @Test
  fun missingPetName_isRejectedBeforePublicationPath() {
    val normalized = source.replace(Regex("\\s+"), " ")
    assertTrue(normalized.contains("val petNameError = requiredPetNameMessage(petName)"))
    assertTrue(normalized.contains("if (petNameError != null) { formMessage = petNameError return"))
    assertTrue(source.contains("if (petName.isBlank()) \"Campo obligatorio\" else null"))
    assertTrue(source.contains("fun submitPost()"))
    assertTrue(source.indexOf("fun submitPost()") < source.indexOf("viewModel.createNewPetPost"))
    assertTrue(source.contains("formMessage = null"))
  }

  private fun createPostSource(): String {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    val root = generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
    return listOf(
      "app/src/main/java/com/findyourpet/app/ui/screens/CreatePetPostScreen.kt",
      "app/src/main/java/com/findyourpet/app/ui/components/LocationSelectionDialogs.kt"
    ).joinToString("\n") { relativePath -> File(root, relativePath).readText() }
  }
}
