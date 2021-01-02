package com.example.t_mobop_werewolf

import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.android.synthetic.main.fragment_waiting_room.*

class Frag_Actions_FortuneTeller : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // onCreateView is similar to onCreate for an activity
    // be careful not to refer to an UI element as they are not yet available (return null)
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
            val roomName = GeneralDataModel.localRoomName
            val pseudo = GeneralDataModel.localPseudo
            Log.d("MainActivity", "1 role reavealed")
            playersRadioGroup.setOnCheckedChangeListener { group, checkedId ->// get the player pseudo
                val playerPseudo = GeneralDataModel.getAnyData("$roomName/Players/Player$checkedId/Pseudo") as String// display it
                val playerRole = GeneralDataModel.getAnyData("$roomName/Players/Player$checkedId/Role") as String // display it

                val roleText = textView
                roleText.layoutParams= LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                roleText.setPadding(24,0,0,16)
                roleText.setText(playerPseudo + "is a" + playerRole)

                //view.findViewById<Layout>(R.id.tellerList)?.addView(roleText)
                //TODO change the flag for next turn
            }
        }

        // add a button that waits on the player to have

        buttonPass.setOnClickListener {
            Log.d("MainActivity", "FortuneTeller has pass her turn")
            //TODO change the flag for next turn
        }
    }
}
