package com.example.t_mobop_werewolf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.*
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import kotlinx.android.synthetic.main.activity_playing.*

class Frag_Actions_Witch : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_actions_witch, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSave = view.findViewById<Button>(R.id.HunterActionbtn1)
        val buttonKill = view.findViewById<Button>(R.id.HunterActionbtn2)
        val buttonPass = view.findViewById<Button>(R.id.buttonValidate)
        val roomName = GeneralDataModel.localRoomName
        //val pseudo = GeneralDataModel.localPseudo
        //val playerList = view.findViewById<ListView>(R.id.listview_Players)
        val path = "$roomName/GeneralData/Flag"

        val flag = GeneralDataModel.getAnyData(path) as Boolean

        buttonSave.setOnClickListener{
            val checkedId = playersRadioGroup.checkedRadioButtonId
            if (checkedId != -1) {
                val potionSavePath = "$roomName/rolesData/potionSave"
                val potionLeft = GeneralDataModel.getAnyData(potionSavePath) as Int

                if (potionLeft > 0) {
                    val tobesaved: String = "Player$checkedId"

                    GeneralDataModel.setAnyData(potionSavePath, potionLeft - 1)
                    GeneralDataModel.savePlayer(tobesaved)
                    GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
                    Log.d("MainActivity", "witch saved someone")
                }
                else {
                    Log.d("MainActivity", "you don't have any saving potion left")
                }
            }
            else {
                Toast.makeText(
                    context,
                    "Select a villager to save and try again",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        buttonKill.setOnClickListener {

            playersRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if (playersRadioGroup.getCheckedRadioButtonId() != -1) {
                    val potionKillPath = "$roomName/rolesData/potionKill"
                    val potionLeft = GeneralDataModel.getAnyData(potionKillPath) as Int

                    if (potionLeft > 0) {
                        val tobekilled: String = "Player$checkedId"

                        GeneralDataModel.setAnyData(potionKillPath, potionLeft - 1)
                        GeneralDataModel.killPlayer(tobekilled)
                        GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
                        Log.d("MainActivity", "witch killed someone")
                    } else {
                        Log.d("MainActivity", "you don't have any killing potion left")
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Select a villager to kill and try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        buttonPass.setOnClickListener{
            GeneralDataModel.setAnyData(path, flag.not()) // Actualize any data to activate DataChanged function
            Log.d("MainActivity", "you chose to not waste your potion")
        }

    }

}