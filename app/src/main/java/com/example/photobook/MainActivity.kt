package com.example.photobook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * MainActivity - Class that use our navigation
 * it will display the mainFragment as entry point.
 *
 * Meanwhile, the entry point of the app is
 * AuthenticationActivity
 */
class MainActivity : AppCompatActivity()
{
    /**
     * onCreate - inflates activity_main
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}