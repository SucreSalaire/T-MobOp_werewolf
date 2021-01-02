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

                    val pathRole = "$roomName/Players/$checkedId/--" //TODO got to find path to role
                    val selectedRole = GeneralDataModel.getAnyData(pathRole) as String
                    val pathSelected = "$roomName/Players/$checkedId/--" //TODO create a path for selected targets
                    val pathListSelected = "$roomName/Players/$checkedId/--" //TODO create a path for list of selected targets

                    // Verify that target isn't a werewolf
                    if (selectedRole == "Werewolf") {
                        Toast.makeText(context,"Select a villager, not an other werewolf",Toast.LENGTH_LONG).show()
                    }
                    else {
                        var votes: Int = GeneralDataModel.getAnyData(pathSelected) as Int
                        GeneralDataModel.setAnyData(pathSelected, votes+1)
                    }

                    //TODO Check if a majority of werewolves have selected a target
                    val voted = arrayListOf<Int>(GeneralDataModel.getAnyData(pathListSelected) as Int)
                    val max: Int? = voted.max()
                    var nMax: Int = 0
                    for (k in voted){
                        if(k == max) nMax = nMax + 1
                    }
                    if(nMax > 1) Toast.makeText(context,"Reselect a target, we had a tie",Toast.LENGTH_LONG).show()
                    else{ //kill selected player
                        val path2bkilled = "$roomName/Players/$checkedId/--" //TODO create a path for target to be killed
                        val tobkilled: String = GeneralDataModel.getAnyData(path2bkilled) as String
                        GeneralDataModel.killPlayer(tobkilled)
                    }

                }
                Toast.makeText(context,"Select a villager to kill and try again",Toast.LENGTH_LONG).show()
            }
        }
    }
}