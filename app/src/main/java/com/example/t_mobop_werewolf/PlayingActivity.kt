package com.example.t_mobop_werewolf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar

class PlayingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val toolbar = findViewById(R.id.play_actionbar)
        setSupportActionBar(toolbar)
    }
}

