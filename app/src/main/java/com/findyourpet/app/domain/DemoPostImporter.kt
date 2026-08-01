package com.findyourpet.app.domain

import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.ui.viewmodel.UserProfile

object DemoPostImporter {
    fun toAuthenticatedProductionPost(seedPost: PetPostEntity, user: UserProfile): PetPostEntity {
        require(user.id.isNotBlank()) { "A signed-in Firebase uid is required." }
        return seedPost.copy(
            id = if (seedPost.id.startsWith("post_")) "${seedPost.id}_${user.id}" else seedPost.id,
            ownerId = user.id,
            ownerName = user.name
        )
    }
}
