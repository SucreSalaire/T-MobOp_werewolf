package com.example.t_mobop_werewolf

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.example.t_mobop_werewolf.FirebaseData.StoryState

class WaitingRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)

        val roomName = findViewById<TextView>(R.id.textViewRoomName)
        roomName.text = GeneralDataModel.localRoomName

        val launchGameButton = findViewById<Button>(R.id.buttonLaunchGame)
        if (GeneralDataModel.iAmtheHost)
        {
            launchGameButton.visibility = View.VISIBLE
            launchGameButton.setOnClickListener{
                Toast.makeText(this, "Launching the game", Toast.LENGTH_SHORT).show()
                GeneralDataModel.setupAndStartGame()
                Intent(WaitingRoomActivity(), PlayingActivity::class.java)
            }
        }

        val waitingListView = findViewById<ListView>(R.id.listViewRoomWaiting)
        waitingListView.adapter = PlayerWaitingAdapter(this)

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
    }


    private class PlayerWaitingAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        private val names = GeneralDataModel.getPlayersPseudos(GeneralDataModel.localRoomName)

        init{mContext = context}

        override fun getCount(): Int { return names.size }

        override fun getItemId(position: Int): Long { return  position.toLong() }

        override fun getItem(position: Int): Any { return "TEST STRING" }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_waiting_room, viewGroup, false)

            val playerWaiting = rowMain.findViewById<TextView>(R.id.textViewPlayerWaiting)
            playerWaiting.text = names[position] + " is waiting."

            return rowMain
        }
    }
}