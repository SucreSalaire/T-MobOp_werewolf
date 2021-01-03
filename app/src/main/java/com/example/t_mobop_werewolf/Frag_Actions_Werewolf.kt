package com.example.t_mobop_werewolf

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
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

        val buttonValidVote = view.findViewById<Button>(R.id.buttonValidVote)
        val roomName = GeneralDataModel.localRoomName
        val path = "$roomName/GeneralData/Flag"
        val flag = GeneralDataModel.getAnyData(path) as Boolean


        buttonValidVote.setOnClickListener {

            //val pseudo = GeneralDataModel.localPseudo

            // Check if all werewolves have voted

            playersRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (playersRadioGroup.getCheckedRadioButtonId() != -1) {

                    //val pathRole = "$roomName/Players/Player$checkedId/Role"
                    //val selectedRole = GeneralDataModel.getAnyData(pathRole) as String
                    val pathSelected = "$roomName/Players/Player$checkedId/Votes"
                    val votes = GeneralDataModel.getPlayersVotes(GeneralDataModel.localRoomName)
                    val max: Int? = votes.max()

                    var nMax: Int = 0;
                    var killed: Int = 0;
                    var n: Int = 1
                    var nVotes: Int = GeneralDataModel.getAnyData(pathSelected) as Int

                    GeneralDataModel.killPlayer("Player$checkedId")
                    GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
                    Log.d("MainActivity", "Target kill successful")

                    /* Verify that target isn't a werewolf
                    if (selectedRole == "Werewolf") {
                        Toast.makeText(
                            context,
                            "Select a villager, not an other werewolf",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else {
                    GeneralDataModel.setAnyData(pathSelected, nVotes + 1)
                    //}*/

                    // Check if all werewolves have voted
                    /*if(GeneralDataModel.validateVote(roomName, "Werewolf")) {
                        // Check if a majority of werewolves have selected a target
                        for (k in votes) {
                            if (k == max) {
                                nMax += 1
                                killed = n
                            }
                            n += 1
                        }
                        if (nMax > 1) Toast.makeText(
                            context,
                            "Reselect a target, we had a tie",
                            Toast.LENGTH_LONG
                        ).show()
                        else { //kill selected player
                            val tobkilled: String = "Player$killed"
                            GeneralDataModel.killPlayer(tobkilled)
                            Toast.makeText(context, "You've aced the kill !", Toast.LENGTH_LONG)
                                .show()
                            GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
                            Log.d("MainActivity", "Target kill successful")
                        }
                    }
                    else {
                        Toast.makeText(
                            context,
                            "Waiting for other werewolves to decide",
                            Toast.LENGTH_LONG
                        ).show()
                        GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
                        Log.d("MainActivity", "Target kill successful")
                    }*/
                }
                else Toast.makeText(context,"Select a villager to kill and try again",Toast.LENGTH_LONG ).show()
            }

        }
    }
}