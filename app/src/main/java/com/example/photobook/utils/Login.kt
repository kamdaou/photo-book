package com.example.photobook.utils

import com.google.firebase.auth.FirebaseUser

object Login {
    var currentUser: FirebaseUser? = null

    fun set(user: FirebaseUser)
    {
        currentUser = user
    }
}