package com.example.t_mobop_werewolf

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.android.synthetic.main.fragment_actions_werewolf.*

class Frag_Actions_Werewolf : Fragment() {
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

        val v = inflater.inflate(R.layout.fragment_actions_werewolf, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSave = view.findViewById<Button>(R.id.buttonValidVote)

        buttonValidVote.setOnClickListener{
            val roomName = GeneralDataModel.localRoomName
            val pseudo = GeneralDataModel.localPseudo


            val path1 = "$roomName/Players/$pseudo/--" //TODO got to find this
            val path2 = "$roomName/Players/$pseudo/--" //TODO got to find this


            playersRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != null) {
                    //TODO Verify that target isn't a werewolf (make checkboxes into radio btn)
                    val pathRole = "$roomName/Players/$checkedId/--" //TODO got to find path to role
                    val selectedRole = GeneralDataModel.getAnyData(pathRole) as String
                    if (selectedRole == "Werewolf") {
                        Toast.makeText(context,"Select a villager, not an other werewolf",Toast.LENGTH_LONG).show()
                    }

                    //TODO Check if a majority of werewolves have selected a target

                    //TODO kill selected player
                }
                Toast.makeText(context,"Select a villager to kill and try again",Toast.LENGTH_LONG).show()
            }
        }
    }
}