package com.example.t_mobop_werewolf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel


class CreatingRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_room)


        val buttonCreateRoom = findViewById<Button>(R.id.buttonCreateConfirm)
        buttonCreateRoom.setOnClickListener{
            val roomName = findViewById<EditText>(R.id.editTextNewRoomName)
            val nbPlayers = findViewById<EditText>(R.id.editTextPlayersNumber).text.toString().toInt()
            val hostName = findViewById<EditText>(R.id.editTextHostName)
            val valid: Boolean = GeneralDataModel.createRoom(roomName.text.toString(), nbPlayers, hostName.text.toString())
            if (valid)
            {
                Toast.makeText(applicationContext, "Room created.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, WaitingRoomActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Room creation denied, already open", Toast.LENGTH_LONG).show()
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }
}