package com.example.photobook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity - Class that use our navigation
 * it will display the mainFragment as entry point.
 *
 * Meanwhile, the entry point of the app is
 * AuthenticationActivity
 */
class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
