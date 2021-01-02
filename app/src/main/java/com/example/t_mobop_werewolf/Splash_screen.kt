package com.example.t_mobop_werewolf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        //animation declaration
        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        val loadImage = findViewById<ImageView>(R.id.Loading_image)
        val appTitle = findViewById<TextView>(R.id.appTitle_text)
        val appTagline = findViewById<TextView>(R.id.tagline)

        // set animation

        loadImage.startAnimation(topAnim)
        appTitle.startAnimation(bottomAnim)
        appTagline.startAnimation(bottomAnim)

        loadload.animate().setDuration(4000).alpha(1f).withEndAction {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
            finish()
        }
    }
}