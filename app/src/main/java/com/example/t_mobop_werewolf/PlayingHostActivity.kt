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
    var storyState: Long = 0
    var flagData = Firebase.database.reference.child("$roomName/GeneralData/Flag")
    val fragment_actions = Frag_Actions_NoActions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val fragment_actions = Frag_Actions_NoActions()

        // These lines will be modified to display from the data received from Firebase
        // This text will be created only at the game start, won't change after
        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)

        val player_name = GeneralDataModel.localPseudo

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village." // later controlled by Firebase

        val playersList = findViewById<ListView>(R.id.listview_Players)
        playersList.setBackgroundColor(Color.parseColor("#FFFFFF"))

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
            radioButton.setText(players)
            radioButton.id = k //TODO verifier le type
            k++

            findViewById<RadioGroup>(R.id.playersRadioGroup)?.addView(radioButton)
        }

        //---
        // ---x--- Firebase database listener for the Flag variable ---x---
        flagData.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    Log.d("StoryState", "Data updated")
                    //Toast.makeText(applicationContext, "data changed: $storyState", Toast.LENGTH_SHORT).show()
                    storyState = chooseActions()   // this function is called every time StoryState is updated
                    Toast.makeText(applicationContext, "nextStorySt = $storyState", Toast.LENGTH_SHORT).show()
                    changeFragment(storyState)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error database for storyState", Toast.LENGTH_SHORT).show()
            }
        })
        //---
    }

    override fun onDestroy() {
        super.onDestroy()
        // add code to remove listener
    }

    // THIS FUNCTION IS CALLED EVERY TIME a database VALUE IS UPDATED !!!! ADD ACTIONS HERE
    // This function decide what's the next storyState
    private fun chooseActions() : Long{
        Toast.makeText(this, "Function chooseActions() called", Toast.LENGTH_SHORT).show()
        var nextState : Long = 0

        if (storyState == 1 as Long) { // werewolf turn
            val voted = GeneralDataModel.validateVote(roomName, "Werewolf")
            if(voted){
                nextState = GeneralDataModel.nextState(storyState)
                Toast.makeText(this, "Werewolf voted", Toast.LENGTH_SHORT).show()
            }
        }
        else if (storyState == 3 as Long){ // villager voting time
            val voted = GeneralDataModel.validateVote(roomName, "Villager")
            if (voted){
                nextState = GeneralDataModel.nextState(storyState)
                Toast.makeText(this, "Everybody voted", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            nextState = GeneralDataModel.nextState(storyState)
            Toast.makeText(this, " Going to next story state", Toast.LENGTH_SHORT).show()
        }

        return(nextState)
    }

    public fun changeFragment(story : Long){
        var currentFrag = R.id.frag_actions_noactions

        if (getStoryRoleName(story) == GeneralDataModel.localRole){
            Toast.makeText(this, "your turn", Toast.LENGTH_SHORT).show()
            when(GeneralDataModel.localRole){
                "werewolf" -> currentFrag = R.id.frag_actions_werewolf
                "witch" -> currentFrag = R.id.frag_actions_witch
                "fortuneTeller" -> currentFrag = R.id.frag_actions_fortuneteller
            }
        }
        else{
            Toast.makeText(this, "noactions", Toast.LENGTH_SHORT).show()
            when(story){
                1.toLong() -> currentFrag = R.id.frag_actions_noactions
                2.toLong() -> currentFrag = R.id.frag_actions_villager
            }
        }
        var txt = ""
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.FragmentConfigRoom, fragment_actions)
            txt = "fragChanged"
        }
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
        //val transaction = supportFragmentManager.beginTransaction()
        //transaction.replace(currentFrag, fragment_actions)
        //transaction.commit()
    }

    private fun getStoryRoleName(story : Long) : String{
        when(story){
            3.toLong() -> return "werewolf"
            4.toLong() -> return "witch"
            5.toLong() -> return "fortuneTeller"
        }
        return "None"
    }

    // --------------------x-----------------------------------
    // Adapter for the list displaying all the players
    private class PlayersListAdapter(context : Context) : BaseAdapter() {
        private val mContext : Context = context

        // should be received by Firebase
        private val names = arrayListOf<String>(
            "Jean", "Jeanette", "Charles", "Alphonse", "Madeleine", "Clémentine")

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

