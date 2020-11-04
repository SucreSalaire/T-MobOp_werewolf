package com.example.t_mobop_werewolf

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        // These lines will be modified to display from the data received from Firebase
        // This text will be created only at the game start, won't change after
        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = "Villager" // TO BE CHANGED LATER (FIREBASE)
    }



    // FIREBASE WORK TEST


}

