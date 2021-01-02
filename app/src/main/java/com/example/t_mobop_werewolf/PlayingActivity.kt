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
    var storyState: Long = 0
    var storyStateRef = Firebase.database.reference.child("$roomName/GeneralData/StoryState")

    @SuppressLint("ResourceType") // TODO: What's that ?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        // These lines will be modified to display from the data received from Firebase
        // This text will be created only at the game start, won't change after
        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)

        val player_name = GeneralDataModel.localPseudo

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village." // TODO: later controlled by StoryState

        val playersList = findViewById<ListView>(R.id.listview_Players)
        playersList.setBackgroundColor(Color.parseColor("#FFFFFF"))
        //playersList.adapter = PlayersListAdapter(this)

        // should be received by Firebase
        val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        fun getCount(): Int {return names.size}
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
                Log.d("StoryState", "Data updated")
                Toast.makeText(applicationContext, "StoryState changed: $storyState", Toast.LENGTH_SHORT).show()
                PlayingHostActivity().changeFragment(storyState) // this function is called every time StoryState is updated
            }

        }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error database for storyState", Toast.LENGTH_SHORT).show()
            }
        })
    }   // onCreate

    override fun onDestroy() {
        super.onDestroy()
        // add code to remove listener
    }


    // THIS FUNCTION IS CALLED EVERY TIME THE STORYSTATE VALUE IS UPDATED !!!! ADD ACTIONS HERE
    private fun nextActions(){
        Toast.makeText(this, "Function nextActions() called", Toast.LENGTH_SHORT).show()
        // Here can be added another call for a function in the fragment that will receive the
        // new StoryState value and do his thing
    }



    // --------------------x-----------------------------------
    // Adapter for the list displaying all the players
    private class PlayersListAdapter(context : Context) : BaseAdapter() {
        private val mContext : Context = context

        // should be received by Firebase
        private val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        override fun getCount(): Int {return names.size}

        override fun getItem(position: Int): Any {return ""}

        override fun getItemId(position: Int): Long {return position.toLong()}

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_players_list,viewGroup, false)
            val playerName = rowMain.findViewById<TextView>(R.id.playerName)
            playerName.text = names.get(position)
            return rowMain
        }





    }

}

