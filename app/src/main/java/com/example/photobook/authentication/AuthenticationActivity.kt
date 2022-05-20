package com.example.photobook.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.photobook.MainActivity
import com.example.photobook.R
import com.example.photobook.databinding.ActivityAuthenticationBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "AuthenticationActivity"
/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null)
        {
            navigateToMainActivity()
        }
        binding.loginButton.setOnClickListener{
            launchSignInFlow()
        }

    }

    /**
     * launchSignInFlow - Launches sign in flow
     * using firebase authentication
     */
    private fun launchSignInFlow()
    {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                navigateToMainActivity()
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.message}")

                Snackbar.make(findViewById(android.R.id.content), "Authentication failed: ${response?.error?.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * navigateToMainActivity - Navigates to MainActivity and
     * finishes the authentication activity
     */
    private fun navigateToMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finishAffinity()
    }

    companion object
    {
        const val SIGN_IN_RESULT_CODE = 1001
    }
}
