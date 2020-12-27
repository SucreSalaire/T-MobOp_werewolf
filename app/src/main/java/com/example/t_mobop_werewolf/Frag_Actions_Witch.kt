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
            Log.d("MainActivity", "witch saved someone")
            val roomName = GeneralDataModel.roomName
            val path = "$roomName/Players/Player1/potionSaveLeft"
            // val potionLeft = GeneralDataModel.getAnyData(path)
            // if(potionLeft > 0) {
            //
            // }
            // maj de la database
        }

        buttonKill.setOnClickListener{
            Log.d("MainActivity", "witch killed someone")}

        buttonPass.setOnClickListener{
            Log.d("MainActivity", "witch passed")}

    }

}