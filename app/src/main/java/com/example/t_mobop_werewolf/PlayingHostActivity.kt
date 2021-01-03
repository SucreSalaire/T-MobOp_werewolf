package com.example.t_mobop_werewolf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlayingHostActivity : AppCompatActivity() {

    val TAG = "PlayingHostActivity"
    var roomName = GeneralDataModel.localRoomName
    var storyStateRef = Firebase.database.reference.child("$roomName/GeneralData/StoryState")
    var flagRef = Firebase.database.reference.child("$roomName/GeneralData/Flag")
    var currentStoryState: Long = 1
    val manager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing_host)

        setupListenerOnStoryState()
        setupListenerOnFlag()
        initializePlayerList()

    }



    fun setupListenerOnStoryState()
    {
        // ---x--- Firebase database listener for the StoryState variable ---x---
        storyStateRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentStoryState = snapshot.value as Long
                    Log.d(TAG,  "StoryState changed to $currentStoryState")
                    displayFragment(GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo), currentStoryState)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "StoryState listener failed")
            }
        })
    }

    fun setupListenerOnFlag()
    {
        // ---x--- Firebase database listener for the Flag variable ---x---
        flagRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Flag has changed")
                    chooseActions(currentStoryState)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Flag listener failed")
            }
        })
    }


    fun chooseActions(currentStoryState: Long)
    {
        if (currentStoryState == 3.toLong()) { // werewolf turn
            val voted = GeneralDataModel.validateVote(roomName, "Werewolf")

            if(voted){
                GeneralDataModel.nextState(currentStoryState)
                Log.d(TAG, "All the werewolves have voted")
            }
            Log.d(TAG, "werewolfTurn")
        }
        else if (currentStoryState == 7.toLong()){ // villager voting time
            val voted = GeneralDataModel.validateVote(roomName, "Villager")
            if (voted){
                GeneralDataModel.nextState(currentStoryState)
                Log.d(TAG, "All the village has voted")
            }
        }
        else{
            GeneralDataModel.nextState(currentStoryState)
            Log.d(TAG, "Going to next storyState without any action")
        }
    }


    fun displayFragment(playerRole: String, currentStoryState: Long)
    {
        Log.d(TAG, "fun displayFragment($playerRole, $currentStoryState)")

        if (getRoleRelatedToStory(currentStoryState) == playerRole)
        {
            Log.d(TAG, "It's your turn to play: $playerRole")

            when (playerRole)
            {
                "Villager"          -> showFragVillager()
                "Werewolf"          -> showFragWerewolf()
                "Witch"             -> showFragWitch()
                "FortuneTeller"     -> showFragFortuneTeller()
                else                -> showFragNoActions()
            }
        } else
        {
            Log.d(TAG, "It isn't your turn to play")
            showFragNoActions()
        }
    }


    fun showFragNoActions()
    {
        Log.d(TAG,"showFragNoActions()")
        val transaction = manager.beginTransaction()
        val fragment = Frag_Actions_NoActions()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun showFragVillager()
    {
        Log.d(TAG,"showFragVillager()")
        val transaction = manager.beginTransaction()
        val fragment = Frag_Actions_Villager()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun showFragWerewolf()
    {
        Log.d(TAG,"showFragWerewolf()")
        val transaction = manager.beginTransaction()
        val fragment = Frag_Actions_Werewolf()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun showFragWitch()
    {
        Log.d(TAG,"showFragWitch()")
        val transaction = manager.beginTransaction()
        val fragment = Frag_Actions_Witch()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun showFragFortuneTeller()
    {
        Log.d(TAG,"showFragFortuneTeller()")
        val transaction = manager.beginTransaction()
        val fragment = Frag_Actions_FortuneTeller()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun getRoleRelatedToStory(currentStoryState: Long): String
    {
        when(currentStoryState){
            // These have to match with GeneralDataModel
            3.toLong() -> return "Werewolf"
            4.toLong() -> return "Witch"
            5.toLong() -> return "FortuneTeller"
            7.toLong() -> return "Villager"

            else -> return "NoRoleRelatedToThisStoryState"
        }
    }

    fun initializePlayerList()
    {

        val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        var k: Int = 1

        for (players in names) {
            val radioButton = RadioButton(this)
            radioButton.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.setPadding(24, 0, 0, 16)
            radioButton.text = players
            val nb = GeneralDataModel.localPlayerNb.toString()

            if (radioButton.text == GeneralDataModel.localPseudo
                || GeneralDataModel.getAnyData("$roomName/Players/Player$nb/Role") != "Witch"
            ) radioButton.isClickable.not()


            if (GeneralDataModel.getAnyData("$roomName/Players/Player$nb/Role") == "Werewolf"
                && GeneralDataModel.getAnyData("$roomName/Players/Player$k/Role") == "Werewolf"
            ) radioButton.isClickable.not()

            radioButton.id = k
            k += 1

            findViewById<RadioGroup>(R.id.playersRadioGroup)?.addView(radioButton)
        }
    }
} // PlayingHostActivity
