package com.findyourpet.app

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreRulesStaticTest {
  private val rulesText = File(repoRoot(), "firestore.rules").readText()

  @Test
  fun rulesRequireAuthenticationAndDefaultDeny() {
    assertTrue(rulesText.contains("return request.auth != null"))
    assertTrue(rulesText.contains("match /{document=**}"))
    assertTrue(rulesText.contains("allow read, write: if false"))
  }

  @Test
  fun postRulesKeepOwnerImmutableAndOwnerOnlyWrites() {
    assertTrue(rulesText.contains("request.resource.data.ownerId == uid()"))
    assertTrue(rulesText.contains("request.resource.data.ownerId == resource.data.ownerId"))
    assertTrue(rulesText.contains("allow delete: if isPostOwner()"))
    assertTrue(rulesText.contains("hasNoPublicContactFields(request.resource.data)"))
    assertTrue(rulesText.contains("validPetPostLocation(request.resource.data)"))
    assertTrue(rulesText.contains("validCloudinaryMediaReference(request.resource.data)"))
    assertTrue(rulesText.contains("validLocationSource(request.resource.data)"))
    assertTrue(rulesText.contains("'isContactRevealedToAll'"))
    assertTrue(rulesText.contains("'ownerPhone'"))
    assertTrue(rulesText.contains("'ownerEmail'"))
    assertTrue(rulesText.contains("'ownerAddress'"))
    assertTrue(rulesText.contains("'ownerLatitude'"))
    assertTrue(rulesText.contains("'ownerLongitude'"))
  }

  @Test
  fun postLocationRulesAcceptOnlyValidPetCoordinates() {
    assertTrue(rulesText.contains("function validPetPostLocation(data)"))
    assertTrue(rulesText.contains("function validOptionalPetPostLocation(data)"))
    assertTrue(rulesText.contains("data.latitude is number"))
    assertTrue(rulesText.contains("data.longitude is number"))
    assertTrue(rulesText.contains("data.latitude >= -90"))
    assertTrue(rulesText.contains("data.latitude <= 90"))
    assertTrue(rulesText.contains("data.longitude >= -180"))
    assertTrue(rulesText.contains("data.longitude <= 180"))
  }

  @Test
  fun sightingRulesDeriveOwnerFromReferencedPostAndStayAppendOnly() {
    assertTrue(rulesText.contains("request.resource.data.reporterId == uid()"))
    assertTrue(rulesText.contains("request.resource.data.reporterId != request.resource.data.ownerId"))
    assertTrue(rulesText.contains("documents/petPosts/$(request.resource.data.postId)"))
    assertTrue(rulesText.contains("match /sightings/{sightingId}"))
    assertTrue(rulesText.contains("allow update, delete: if false"))
    assertTrue(rulesText.contains("preciseSightingLocationIsConsented(request.resource.data)"))
  }

  @Test
  fun firestoreRulesRequireCloudinaryMediaMetadata() {
    assertTrue(rulesText.contains("validCloudinaryMediaReference"))
    assertTrue(rulesText.contains("data.photoUri.matches('https://res.cloudinary.com/.*')"))
    assertTrue(rulesText.contains("data.mediaProvider == 'CLOUDINARY'"))
    assertTrue(rulesText.contains("data.mediaPublicId is string"))
    assertTrue(rulesText.contains("data.mediaContentType.matches('image/.*')"))
    assertTrue(rulesText.contains("validOptionalMediaReference(request.resource.data)"))
  }

  @Test
  fun chatRulesRequireParticipantsAndImmutableMessages() {
    assertTrue(rulesText.contains("validParticipantIds"))
    assertTrue(rulesText.contains("data.ownerId != data.reporterId"))
    assertTrue(rulesText.contains("uid() in data.participantIds"))
    assertTrue(rulesText.contains("allow get: if isChatParticipant(resource.data)"))
    assertTrue(rulesText.contains("allow list: if signedIn()"))
    assertTrue(rulesText.contains("uid() in resource.data.participantIds"))
    assertTrue(rulesText.contains("request.resource.data.participantIds == resource.data.participantIds"))
    assertTrue(rulesText.contains("request.resource.data.senderId == uid()"))
    assertTrue(rulesText.contains("getAfter(/databases/$(database)/documents/chatSessions/$(chatId))"))
    assertTrue(rulesText.contains("request.resource.data.senderId in chatAfter(chatId).participantIds"))
  }

  @Test
  fun sightingAlertRulesValidateCrossReferencesAndSnapshotPrivacy() {
    assertTrue(rulesText.contains("validSightingAlertCreate(chatId)"))
    assertTrue(rulesText.contains("sightingAfter(request.resource.data.sightingId)"))
    assertTrue(rulesText.contains("request.resource.data.type == 'sighting_alert'"))
    assertTrue(rulesText.contains("validAlertSnapshot(request.resource.data)"))
    assertTrue(rulesText.contains("'contactGrantIds'"))
    assertTrue(rulesText.contains("'coordinates'"))
    assertTrue(rulesText.contains("request.resource.data.senderId == request.resource.data.reporterId"))
  }

  @Test
  fun sightingAlertReadsRemainParticipantOnlyAndImmutable() {
    assertTrue(rulesText.contains("allow read: if isChatParticipant(get(/databases/$(database)/documents/chatSessions/$(chatId)).data)"))
    assertTrue(rulesText.contains("allow update, delete: if false"))
    assertTrue(rulesText.contains("validChatSessionReferences(request.resource.data)"))
    assertTrue(rulesText.contains("request.resource.data.reporterId == uid()"))
  }

  @Test
  fun contactGrantRulesAreChatScopedAndOwnerControlled() {
    assertTrue(rulesText.contains("match /contactGrants/{grantId}"))
    assertTrue(rulesText.contains("allow read, write: if false"))
    assertTrue(rulesText.contains("hasNoChatContactFields(request.resource.data)"))
    assertTrue(rulesText.contains("!chatContactFieldsChanged()"))
    assertTrue(!rulesText.contains("grantMatchesChat"))
    assertTrue(!rulesText.contains("request.resource.data.type in ['ALERT', 'CHAT', 'CONTACT_SHARED']"))
    assertTrue(rulesText.contains("request.resource.data.type in ['ALERT', 'CHAT']"))
  }

  @Test
  fun notificationsAreScopedToRecipient() {
    assertTrue(rulesText.contains("match /notifications/{notificationId}"))
    assertTrue(rulesText.contains("request.resource.data.recipientId == userId"))
    assertTrue(rulesText.contains("request.resource.data.recipientId == resource.data.recipientId"))
    assertTrue(rulesText.contains("affectedKeys().hasOnly(['isRead'])"))
    assertTrue(rulesText.contains("hasNoSensitiveNotificationFields(request.resource.data)"))
  }

  @Test
  fun chatNotificationsValidateConversationRecipient() {
    assertTrue(rulesText.contains("function validChatNotificationCreate(data)"))
    assertTrue(rulesText.contains("data.type == 'CHAT'"))
    assertTrue(rulesText.contains("data.targetId == data.chatId"))
    assertTrue(rulesText.contains("data.recipientId != uid()"))
    assertTrue(rulesText.contains("validChatNotificationCreate(request.resource.data)"))
  }

  private fun repoRoot(): File {
    val userDir = requireNotNull(System.getProperty("user.dir"))
    return generateSequence(File(userDir).absoluteFile) { it.parentFile }
      .first { File(it, "settings.gradle.kts").isFile }
  }
}
