package com.findyourpet.app

import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaReference
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.data.product.MediaUploadState
import com.findyourpet.app.data.product.RealProductValidators
import android.net.Uri
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RealProductValidatorsTest {
  @Test
  fun postRequiresRealMediaAndRequiredFields() {
    val demoResult = RealProductValidators.validatePost(
      petName = "Milo",
      photoUri = "https://images.unsplash.com/photo-demo.jpg",
      ownerId = "uid_owner",
      locationName = "Barrio Central"
    )
    val realResult = RealProductValidators.validatePost(
      petName = "Milo",
      photoUri = "content://media/pet.jpg",
      ownerId = "uid_owner",
      locationName = "Barrio Central"
    )

    assertFalse(demoResult.isValid)
    assertTrue(realResult.isValid)
  }

  @Test
  fun sightingRequiresLocationSourceAndRejectsDemoPhoto() {
    val missingLocation = RealProductValidators.validateSighting(
      reporterId = "uid_reporter",
      postId = "post_1",
      ownerId = "uid_owner",
      locationName = "Barrio Central",
      locationSource = LocationSource.NONE,
      photoUri = null
    )
    val demoPhoto = RealProductValidators.validateSighting(
      reporterId = "uid_reporter",
      postId = "post_1",
      ownerId = "uid_owner",
      locationName = "Barrio Central",
      locationSource = LocationSource.MANUAL_COARSE,
      photoUri = "https://images.unsplash.com/photo-demo.jpg"
    )
    val validManualFallback = RealProductValidators.validateSighting(
      reporterId = "uid_reporter",
      postId = "post_1",
      ownerId = "uid_owner",
      locationName = "Barrio Central",
      locationSource = LocationSource.MANUAL_COARSE,
      photoUri = ""
    )

    assertFalse(missingLocation.isValid)
    assertFalse(demoPhoto.isValid)
    assertTrue(validManualFallback.isValid)
  }

  @Test
  fun mediaUploadStateCarriesUploadedCloudinaryReference() {
    val reference = MediaReference(
      localUri = Uri.parse("content://media/pet.jpg"),
      remoteUrl = "https://res.cloudinary.com/mqt4dzrt/image/upload/example.jpg",
      provider = "CLOUDINARY",
      publicId = "findyourpet/example",
      contentType = "image/jpeg",
      source = MediaSource.CAMERA
    )
    val state = MediaUploadState.Uploaded(reference)

    assertTrue(reference.isUploaded)
    assertTrue(state.reference.displayUri.contains("res.cloudinary.com"))
  }
}
