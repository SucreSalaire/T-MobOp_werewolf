package com.example.t_mobop_werewolf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ________________________
        // Buttons at the startup to access the two activities
        // Last modified : 19 October 2020 by NAGI
        val button_Wait_Room = findViewById<Button>(R.id.buttonWaitingRoom)
        val button_Playing = findViewById<Button>(R.id.buttonPlaying)

        button_Wait_Room.setOnClickListener{
            val intent = Intent(this, WaitingRoomActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: WaitingRoomActivity", Toast.LENGTH_SHORT).show()
        }
        button_Playing.setOnClickListener{
            val intent = Intent(this, PlayingActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: PlayingActivity", Toast.LENGTH_SHORT).show()
        }
        // ________________________
    }



}