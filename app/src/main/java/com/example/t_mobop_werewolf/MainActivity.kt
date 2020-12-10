package com.example.t_mobop_werewolf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.example.t_mobop_werewolf.Adapters.GeneralDataAdapter
import com.example.t_mobop_werewolf.FirebaseData.GeneralData
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity(), Observer {

    private lateinit var database: DatabaseReference
    private lateinit var ref_StoryState: DatabaseReference

    private var mGeneralDataObserver: Observer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeButtons()     // setup buttons for dev purpose
        SetupDatabase()         // THIS FUNCTION MUST BE CALLED ONLY BY THE FIRST TO OPEN A ROOM
        GeneralDataModel        // initialisation database model
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        GeneralDataModel.deleteObserver(this)
    }

    override fun onResume() {
        super.onResume()
        mGeneralDataObserver = Observer{_, _->}
        GeneralDataModel.addObserver(mGeneralDataObserver)
    }

    override fun onStop() {
        super.onStop()
        if (mGeneralDataObserver != null){
            GeneralDataModel.deleteObserver(mGeneralDataObserver)
        }
    }

    fun initializeButtons(){
        // Buttons at the startup to access the two activities
        val button_Wait_Room = findViewById<Button>(R.id.buttonWaitingRoom)
        val button_Playing = findViewById<Button>(R.id.buttonPlaying)

        button_Wait_Room.setOnClickListener {
            val intent = Intent(this, WaitingRoomActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: WaitingRoomActivity", Toast.LENGTH_SHORT).show()
        }
        button_Playing.setOnClickListener {
            val intent = Intent(this, PlayingActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: PlayingActivity", Toast.LENGTH_SHORT).show()
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        TODO("Not yet implemented")
    }

    // ---------x--------- Custom Firebase functions ---------x---------
        // Check if a room is open
            // Yes : join it and ask player pseudo
            // No : ask room name, host/player pseudo and take him to setup room

    fun checkOpenRoom(): Boolean{
        var GeneralDataList = GeneralDataModel.getData()




        return false
    }

    fun JoinRoom(RoomName: String, Pseudo: String): Boolean{

        return false
    }

    fun OpenNewRoom(RoomName: String, NbPlayers: Int,HostName: String ): Boolean{

        // Do we limit max players
        return false
    }



    // Function used to setup Firebase Realtime Database
    fun SetupDatabase(){
        database = Firebase.database.reference
        database.removeValue()              // removes everything at the root
        Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show()

        database.child("GeneralData").child("RoomName").setValue("None")
        database.child("GeneralData").child("StoryState").setValue("0.0")
        database.child("GeneralData").child("HostName").setValue("None")
        database.child("GeneralData").child("WaitRoomOpen").setValue("false")
        database.child("GeneralData").child("RolesDistributed").setValue("false")
        database.child("GeneralData").child("GameStarted").setValue("false")

        database.child("RolesData").child("RevivePotions").setValue("1")
        database.child("RolesData").child("KillPotions").setValue("1")

        database.child("Players").child("Model").child("Pseudo").setValue("Don't touch")
        database.child("Players").child("Model").child("Alive").setValue("true")
        database.child("Players").child("Model").child("Role").setValue("Villager")

        Toast.makeText(this, "Database set as default", Toast.LENGTH_SHORT).show()
    }

}


