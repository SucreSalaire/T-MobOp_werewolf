package com.example.t_mobop_werewolf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel

class JoiningRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joining_room)

        val buttonJoinRoom = findViewById<Button>(R.id.buttonJoinConfirm)
        buttonJoinRoom.setOnClickListener{
            val roomName= findViewById<EditText>(R.id.editTextRoomName)
            val playerPseudo = findViewById<EditText>(R.id.editTextPlayerPseudo)
            val valid = GeneralDataModel.joinRoom(roomName.text.toString(), playerPseudo.text.toString())
            if (valid)
            {
                Toast.makeText(applicationContext, "Room joigned.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, WaitingRoomActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Room joining denied", Toast.LENGTH_LONG).show()
                val intent = intent
                finish()
                startActivity(intent)
            }
        }

    }
}