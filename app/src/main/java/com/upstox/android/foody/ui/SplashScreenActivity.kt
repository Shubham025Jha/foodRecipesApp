package com.upstox.android.foody.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.upstox.android.foody.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        GlobalScope.launch {
            delay(5000L)

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}