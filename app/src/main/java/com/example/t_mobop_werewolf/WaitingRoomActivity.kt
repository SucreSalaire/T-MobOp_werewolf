package com.example.t_mobop_werewolf

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class WaitingRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)

        val waiting_listView = findViewById<ListView>(R.id.listViewRoomWaiting)
        waiting_listView.adapter = PlayerWaitingAdapter(this)

        val fragmentWaitingRoom = WaitingRoomFragment()

        supportFragmentManager.beginTransaction().apply {
            remove(fragmentWaitingRoom)
            commit()
        }

        val buttonFloatingPointConfig = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floattingPointConfig)

        buttonFloatingPointConfig.setOnClickListener{
            Toast.makeText(this, "Lunching: ConfigFragment", Toast.LENGTH_SHORT).show()
            if(fragmentWaitingRoom.isVisible){
                supportFragmentManager.beginTransaction().apply {
                    remove(fragmentWaitingRoom)
                    commit() } }
            else {
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.fFfragmentConfigRoom, fragmentWaitingRoom)
                    addToBackStack(null)
                    commit()}

                    val role_list = findViewById<ListView>(R.id.listViewConfigParams)
                    role_list.adapter = ConfigParamsAdapter(this)}
        }
    }

    private class ConfigParamsAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        // TODO : add dynamic Roles
        private val names = arrayListOf<String>("Werewolf", "Villager", "Hunter", "Witch")

        init{mContext = context}

        override fun getCount(): Int {
            return names.size
        }

        override fun getItemId(position: Int): Long {
            return  position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.config_frag_waiting_room, viewGroup, false)

            val Role = rowMain.findViewById<TextView>(R.id.textViewRole)
            Role.text = names[position]

            return rowMain
        }
    }

    private class PlayerWaitingAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        private val names = arrayListOf<String>("Anou", "Harjeet", "Samir")

        init{mContext = context}

        override fun getCount(): Int {
            return names.size
        }

        override fun getItemId(position: Int): Long {
            return  position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_waiting_room, viewGroup, false)

            val playerWaiting = rowMain.findViewById<TextView>(R.id.textViewPlayerWaiting)
            playerWaiting.text = names[position] + " is waiting inside the room"

            return rowMain
        }
    }
}