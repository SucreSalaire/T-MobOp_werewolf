package com.example.t_mobop_werewolf

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.GeneratedAdapter
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.example.t_mobop_werewolf.FirebaseData.StoryState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlayingHostActivity : AppCompatActivity() {

    var roomName = GeneralDataModel.localRoomName
    var storyState: Long = 1
    var flagData = Firebase.database.reference.child("$roomName/GeneralData/Flag")
    val fragment_actions = Frag_Actions_NoActions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)
        GeneralDataModel.localRole = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village." // later controlled by Firebase

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
            radioButton.setText(players)
            radioButton.id = k //TODO verifier le type
            k++
            
            findViewById<RadioGroup>(R.id.playersRadioGroup)?.addView(radioButton)
        }

        // ---x--- Firebase database listener for the Flag variable ---x---
        flagData.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("PlayingHostActivity", "Data updated")
                    storyState = chooseActions()   // this function is called every time StoryState is updated
                    Log.d("PlayingHostActivity", "nextStorySt = $storyState")
                    changeFragment(storyState)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("PlayingHostActivity", "nextStorySt = $storyState")
            }
        })

        val roomName = GeneralDataModel.localRoomName
        val path = "$roomName/GeneralData/Flag"

        val flag = GeneralDataModel.getAnyData(path) as Boolean
        GeneralDataModel.setAnyData(path, flag.not())
    }

    override fun onDestroy() {
        super.onDestroy()
        // add code to remove listener
    }

    private fun chooseActions() : Long{
        var nextState : Long = 0

        if (storyState == 3.toLong()) { // werewolf turn
            val voted = GeneralDataModel.validateVote(roomName, "Werewolf")
            if(voted){
                nextState = GeneralDataModel.nextState(storyState)
                Log.d("PlayingHostActivity", "werewolf voted")
            }
            Log.d("PlayingHostActivity", "werewolfTurn")
        }
        else if (storyState == 7.toLong()){ // villager voting time
            val voted = GeneralDataModel.validateVote(roomName, "Villager")
            if (voted){
                nextState = GeneralDataModel.nextState(storyState)
                Log.d("PlayingHostActivity", "everybodyvoted")
            }
        }
        else{
            nextState = GeneralDataModel.nextState(storyState)
            Log.d("PlayingHostActivity", "Going to next storyState")
        }
        return(nextState)
    }

    public fun changeFragment(story : Long){
        var currentFrag = R.id.frag_actions_noactions

        val a = getStoryRoleName(story)
        val b = GeneralDataModel.localRole

        Log.d("PlayingHostActivity", a)
        Log.d("PlayingHostActivity", b)

        if ( a == b){
            Log.d("PlayingHostActivity", "your turn")
            when(GeneralDataModel.localRole){
                "Werewolf" -> currentFrag = R.id.frag_actions_werewolf
                "Witch" -> currentFrag = R.id.frag_actions_witch
                "FortuneTeller" -> currentFrag = R.id.frag_actions_fortuneteller
            }
        }
        else{
            Log.d("PlayingHostActivity", "noactions")
            //when(story){
            //    1.toLong() -> currentFrag = R.id.frag_actions_noactions
            //    2.toLong() -> currentFrag = R.id.frag_actions_villager
            //}
            currentFrag = R.id.frag_actions_noactions
        }
        var txt = ""
        supportFragmentManager.beginTransaction().apply {
            replace(currentFrag, fragment_actions)
            txt = "fragChanged"
        }
        Log.d("PlayingHostActivity", txt)
    }

    private fun getStoryRoleName(story : Long) : String{
        when(story){
            3.toLong() -> return "Werewolf"
            4.toLong() -> return "Witch"
            5.toLong() -> return "FortuneTeller"
        }
        return "None"
    }
}