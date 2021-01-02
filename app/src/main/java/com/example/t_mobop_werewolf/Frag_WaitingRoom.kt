package com.example.t_mobop_werewolf

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView


class Frag_WaitingRoom : Fragment(R.layout.fragment_waiting_room) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val roleList = view.findViewById<ListView>(R.id.listViewConfigParams)
        //roleList.adapter = ConfigParamsAdapter(requireContext())
    }



    private class ConfigParamsAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

        // TODO : add dynamic Roles
        private val names = arrayListOf<String>("Werewolf", "Villager", "Hunter", "Witch")

        init{mContext = context}

        override fun getCount(): Int {
            return 0 //return names.size
        }

        override fun getItemId(position: Int): Long {
            return  position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.fragment_waiting_room_config, viewGroup, false)

            val Role = rowMain.findViewById<TextView>(R.id.textViewRole)
            Role.text = names[position]

            return rowMain
        }
    }
}