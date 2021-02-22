package com.odogwudev.instagramclone.data.entities

import com.google.firebase.firestore.Exclude

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val username: String = "",
    val profilePictureUrl: String = "",
    val description: String = "",
    var follows: List<String> = listOf(),
    @get:Exclude var isFollowing: Boolean = false
)