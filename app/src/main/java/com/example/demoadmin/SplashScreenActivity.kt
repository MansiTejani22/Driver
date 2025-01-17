package com.example.demoadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
            finish()
        }, 10000) // 3000 is the delayed time in milliseconds.
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}