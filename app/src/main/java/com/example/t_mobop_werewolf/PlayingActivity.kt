package com.example.t_mobop_werewolf

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView


class PlayingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        // These lines will be modified to display from the data received from Firebase
        // This text will be created only at the game start, won't change after
        val player_role = findViewById<TextView>(R.id.textview_PlayerRole)
        player_role.text = "Villager" // TO BE CHANGED LATER (FIREBASE)

        val player_name = "LaSorciere"

        val story = findViewById<TextView>(R.id.textview_storytelling)
        story.text = "The night falls on the quiet village of MonCul" // later controlled by Firebase

        val playersList = findViewById<ListView>(R.id.listview_Players)
        playersList.setBackgroundColor(Color.parseColor("#FFFFFF"))
        playersList.adapter = PlayersListAdapter(this)
    }

    // func dataBase changed

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

