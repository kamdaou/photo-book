package com.example.photobook.utils

import com.google.firebase.auth.FirebaseUser

/**
 * Login - An object provide the current user and accessible
 * everywhere
 */
object Login {
    var currentUser: FirebaseUser? = null

    /**
     * set - Sets value of the current user
     *
     * @user: the current user.
     */
    fun set(user: FirebaseUser)
    {
        currentUser = user
    }
}