package com.example.t_mobop_werewolf

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.example.t_mobop_werewolf.FirebaseData.StoryState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WaitingRoomActivity : AppCompatActivity() {

    var roomName = GeneralDataModel.localRoomName
    val gameStartedRef = Firebase.database.reference.child("$roomName/GeneralData/GameStarted")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        val roomName = findViewById<TextView>(R.id.textViewRoomName)
        roomName.text = GeneralDataModel.localRoomName

        // Launching the game
        val launchGameButton = findViewById<Button>(R.id.buttonLaunchGame)
        if (GeneralDataModel.iAmtheHost)
        {
            launchGameButton.visibility = View.VISIBLE
            launchGameButton.setOnClickListener{
                Toast.makeText(this, "Launching the game", Toast.LENGTH_SHORT).show()
                try{
                    GeneralDataModel.setupAndStartGame()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WaitingRoomActivity", "Fun setupAndStartGame() failed")
                }
                try{
                    val intent = Intent(this, PlayingHostActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("WaitingRoomActivity", "PlayingHostActivity launch failed")
                }
            }
        }

        // List of the waiting players
        val waitingListView = findViewById<ListView>(R.id.listViewRoomWaiting)
        waitingListView.adapter = PlayerWaitingAdapter(this)

        // PopUp panel to configure game rules - not used because uncomplete
        val fragmentWaitingRoom = Frag_WaitingRoom()
        supportFragmentManager.beginTransaction().apply {
            remove(fragmentWaitingRoom)
            commit()
        }

        val buttonFloatingPointConfig = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floattingPointConfig)
        buttonFloatingPointConfig.setOnClickListener{
            if(fragmentWaitingRoom.isVisible){
                Toast.makeText(this, "Closing: ConfigFragment", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction().apply {
                    remove(fragmentWaitingRoom)
                    commit() } }
            else {
                Toast.makeText(this, "Launching ConfigFragment", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.FragmentConfigRoom, fragmentWaitingRoom)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        // ---x--- Firebase database listener for the GameStarted variable ---x---
        gameStartedRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // works if the host has launched the game
                    if (snapshot.value as Boolean && GeneralDataModel.iAmtheHost.not()){
                        Log.d("WaitingRoomActivity", "GameStarted : true")
                        val intent = Intent(this@WaitingRoomActivity, PlayingActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error database for storyState", Toast.LENGTH_SHORT).show()
            }
        })

    } // onCreate


    // List adapter for display the players in the room
    private class PlayerWaitingAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        init{mContext = context}

        private val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        override fun getCount(): Int { return names.size }

        override fun getItemId(position: Int): Long { return  position.toLong() }

        override fun getItem(position: Int): Any { return "TEST STRING" }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_waiting_room, viewGroup, false)

            val playerWaiting = rowMain.findViewById<TextView>(R.id.textViewPlayerWaiting)
            playerWaiting.text = names[position] + " is ready."

            return rowMain
        }
    }
}