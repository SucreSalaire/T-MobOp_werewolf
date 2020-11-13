package com.example.t_mobop_werewolf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase initialization
        database = Firebase.database.reference

        // ________________________
        // Buttons at the startup to access the two activities
        // maybe delcaration should be moved outside of onCreate
        val button_Wait_Room = findViewById<Button>(R.id.buttonWaitingRoom)
        val button_Playing = findViewById<Button>(R.id.buttonPlaying)
        val button_Firebase = findViewById<Button>(R.id.buttonFirebaseTest)

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

        // Firebase setup
        database.removeValue()              // should remove everything at the root
        Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show()
        database.child("GeneralData").child("ScenarioEvent").setValue(0)
        database.child("GeneralData").child("WaitRoomOpen").setValue(false)
        database.child("Players").child("Player1").child("Pseudo").setValue("Pseudo1")
        database.child("Players").child("Player1").child("Role").setValue("Villager")
        database.child("Players").child("Player1").child("Alive").setValue(false)
        database.child("Players").child("Player2").child("Pseudo").setValue("Pseudo2")
        database.child("Players").child("Player2").child("Role").setValue("Werewolf")
        database.child("Players").child("Player2").child("Alive").setValue(true)
        Toast.makeText(this, "Database set as default", Toast.LENGTH_SHORT).show()
    }

    // ________________________

    data class GeneralData(
        var ScenarioEvent: Int? = 0,
        var WaitRoomOpen: Boolean? = false,
        var ReadyToPlay: Boolean?= false
    )

}
