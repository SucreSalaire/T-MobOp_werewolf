package com.example.t_mobop_werewolf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import kotlinx.android.synthetic.main.fragment_actions_werewolf.*

class Frag_Actions_Witch : Fragment() {
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

        val v = inflater.inflate(R.layout.fragment_actions_witch, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSave = view.findViewById<Button>(R.id.buttonSave)
        val buttonKill = view.findViewById<Button>(R.id.buttonKill)
        val buttonPass = view.findViewById<Button>(R.id.buttonValidate)

        buttonSave.setOnClickListener{
            val roomName = GeneralDataModel.localRoomName
            val pseudo = GeneralDataModel.localPseudo
            val path = "$roomName/Players/$pseudo/potionSave"
            val potionLeft =  GeneralDataModel.getAnyData(path) as Int
            if(potionLeft > 0) {
                Log.d("MainActivity", "witch saved someone")
                //setAnyData(path, potionLeft-1)
            }
            else{
                Log.d("MainActivity", "you don't have any saving potion left")
            }
        }

        buttonKill.setOnClickListener{
            val roomName = GeneralDataModel.localRoomName
            val pseudo = GeneralDataModel.localPseudo
            val path = "$roomName/Players/$pseudo/potionKill"
            val potionLeft = GeneralDataModel.getAnyData(path) as Int
            if(potionLeft > 0) {
                Log.d("MainActivity", "witch killed someone")
                //setAnyData(path, potionLeft-1)
            }
            else{
                Log.d("MainActivity", "you don't have any killing potion left")
            }}

        buttonPass.setOnClickListener{
            Log.d("MainActivity", "witch passed")
            val roomName = GeneralDataModel.localRoomName
            val pseudo = GeneralDataModel.localPseudo
            val path = "$roomName/Players/$pseudo/potionSave"
            // Actualise any data to activat DataChanged function
        }

    }

}