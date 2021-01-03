package com.example.t_mobop_werewolf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class PlayingActivity : AppCompatActivity() {

    var roomName = GeneralDataModel.localRoomName
    var storyState: Long = 1
    var storyStateRef = Firebase.database.reference.child("$roomName/GeneralData/StoryState")

    @SuppressLint("ResourceType") // TODO: What's that ?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)
        GeneralDataModel.localRole = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)

        val player_name = GeneralDataModel.localPseudo

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village."

        val playersList = findViewById<ListView>(R.id.listview_Players)
        playersList.setBackgroundColor(Color.parseColor("#FFFFFF"))

        val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        var k: Int = 1
        // Create RadioButton dynamically
        for(players in names){
            val radioButton = RadioButton(this)
            radioButton.layoutParams= LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton.setPadding(24,0,0,16)
            radioButton.text = players
            val nb = GeneralDataModel.localPlayerNb.toString()

            if (radioButton.text == GeneralDataModel.localPseudo
                || GeneralDataModel.getAnyData("$roomName/Players/Player$nb/Role") != "Witch") radioButton.isClickable.not()


            if(GeneralDataModel.getAnyData("$roomName/Players/Player$nb/Role") == "Werewolf"
                && GeneralDataModel.getAnyData("$roomName/Players/Player$k/Role") == "Werewolf") radioButton.isClickable.not()

            radioButton.id = k
            k += 1

            findViewById<RadioGroup>(R.id.playersRadioGroup)?.addView(radioButton)
        }

        // ---x--- Firebase database listener for the StoryState variable ---x---
        storyStateRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                storyState = snapshot.value as Long
                Log.d("StoryState", "StoryState changed")
                PlayingHostActivity().changeFragment(storyState) // this function is called every time StoryState is updated
            }
        }
            override fun onCancelled(error: DatabaseError) {
                Log.d("PlayingActivity", "Error database for storyState")
            }
        })
    }   // onCreate

    override fun onDestroy() {
        super.onDestroy()
        // add code to remove listener
    }
}

