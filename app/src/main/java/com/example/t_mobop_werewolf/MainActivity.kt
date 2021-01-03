package com.example.t_mobop_werewolf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.t_mobop_werewolf.FirebaseData.GeneralDataModel
import java.util.*


class MainActivity : AppCompatActivity(), Observer {

    private var mGeneralDataObserver: Observer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeButtons()        // setup buttons for dev purpose
        GeneralDataModel           // initialisation Firebase database model
        GeneralDataModel.localSnapshotInit()
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

    private fun initializeButtons(){
        val buttonJoinRoom = findViewById<Button>(R.id.buttonJoinRoom)
        val buttonCreateRoom= findViewById<Button>(R.id.buttonCreateRoom)
        val buttonWaitRoom = findViewById<Button>(R.id.buttonWaitingRoom)
        val buttonPlaying = findViewById<Button>(R.id.buttonPlaying)
        val buttonFirebase = findViewById<Button>(R.id.buttonFirebaseTest)

        buttonJoinRoom.setOnClickListener{
            val intent = Intent(this, JoiningRoomActivity::class.java)
            startActivity(intent)
            val nb = GeneralDataModel.getPlayersNumber("a")
            Toast.makeText(this, "Join a room", Toast.LENGTH_LONG).show()
        }
        buttonCreateRoom.setOnClickListener{
            val intent = Intent(this, CreatingRoomActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Create a room", Toast.LENGTH_LONG).show()
        }
        buttonWaitRoom.setOnClickListener {
            val intent = Intent(this, WaitingRoomActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: WaitingRoomActivity", Toast.LENGTH_SHORT).show()
        }
        buttonPlaying.setOnClickListener {
            val intent = Intent(this, PlayingActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Lunching: PlayingActivity", Toast.LENGTH_SHORT).show()
        }
        buttonFirebase.setOnClickListener {
            Log.d("MainActivity", "FIREBASE BUTTON CLICKED")
            Toast.makeText(this, "Database set to default", Toast.LENGTH_SHORT).show()
            GeneralDataModel.resetAllDatabase()   // MUST BE CALLED ONLY BY THE HOST WHEN QUITTING GAME
        }

    }

    override fun update(o: Observable?, arg: Any?) {}

}


