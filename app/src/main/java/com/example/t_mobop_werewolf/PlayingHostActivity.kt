package com.example.t_mobop_werewolf

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
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
    var storyState: Double = 0.0
    var allDataReference = Firebase.database.reference.child(roomName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        // These lines will be modified to display from the data received from Firebase
        // This text will be created only at the game start, won't change after
        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = GeneralDataModel.getPlayerRole(GeneralDataModel.localPseudo)

        val player_name = GeneralDataModel.localPseudo

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village." // later controlled by Firebase

        val playersList = findViewById<ListView>(R.id.listview_Players)
        playersList.setBackgroundColor(Color.parseColor("#FFFFFF"))
        playersList.adapter = PlayersListAdapter(this)

        //---
        // ---x--- Firebase database listener for the StoryState variable ---x---
        allDataReference.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //storyState = snapshot.value as Double
                    Log.d("StoryState", "Data updated")
                    //Toast.makeText(applicationContext, "data changed: $storyState", Toast.LENGTH_SHORT).show()
                    chooseActions()   // this function is called every time StoryState is updated
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
    private fun chooseActions(){
        Toast.makeText(this, "Function chooseActions() called", Toast.LENGTH_SHORT).show()
        // Here can be added another call for a function in the fragment that will receive the
        // new StoryState value and do his thing
        if (storyState == 4.0) { // werewolfturn
            var voted = GeneralDataModel.validateVote(roomName, "Werewolf")
            if(voted){

            }

        }

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

