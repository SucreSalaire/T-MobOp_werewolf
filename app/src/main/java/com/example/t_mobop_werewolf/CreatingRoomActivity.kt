package com.example.t_mobop_werewolf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel

class CreatingRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_room)

        val buttonCreateRoom = findViewById<Button>(R.id.buttonCreateConfirm)
        buttonCreateRoom.setOnClickListener{
            val RoomName = findViewById<EditText>(R.id.editTextNewRoomName).text
            val NbPlayers = findViewById<EditText>(R.id.editTextPlayersNumber).text
            val HostName = findViewById<EditText>(R.id.editTextHostName).text
            GeneralDataModel.openNewRoom(RoomName as String, NbPlayers as Int, HostName as String)

            CONTINUE HERE
        }
    }
}