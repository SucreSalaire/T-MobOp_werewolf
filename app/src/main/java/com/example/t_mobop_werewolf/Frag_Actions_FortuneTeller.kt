package com.example.t_mobop_werewolf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Frag_Actions_FortuneTeller : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_actions_fortuneteller, container, false)
        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonReveal = view.findViewById<Button>(R.id.buttonChoose)
        val buttonPass = view.findViewById<Button>(R.id.buttonPassFortuneTeller)


        buttonReveal.setOnClickListener {
            Log.d("MainActivity", "1 role revealed")
            // get the player pseudo
            // display it
            // change the flag for next turn

        }

        // add a button that waits on the player to have

        buttonPass.setOnClickListener {
            Log.d("MainActivity", "FortuneTeller has pass her turn")
            // change the flag for next turn
        }
    }
}