package com.findyourpet.app

import com.findyourpet.app.domain.OwnershipPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnershipPolicyTest {
  @Test
  fun postManagementRequiresMatchingNonBlankUid() {
    assertTrue(OwnershipPolicy.canManagePost("uid_123", "uid_123"))
    assertFalse(OwnershipPolicy.canManagePost("uid_123", "uid_456"))
    assertFalse(OwnershipPolicy.canManagePost("", "uid_123"))
  }

  @Test
  fun chatAccessRequiresOwnerOrReporterParticipant() {
    assertTrue(OwnershipPolicy.isChatParticipant("owner_uid", "owner_uid", "reporter_uid"))
    assertTrue(OwnershipPolicy.isChatParticipant("reporter_uid", "owner_uid", "reporter_uid"))
    assertFalse(OwnershipPolicy.isChatParticipant("other_uid", "owner_uid", "reporter_uid"))
    assertFalse(OwnershipPolicy.isChatParticipant("", "owner_uid", "reporter_uid"))
  }
}
