package com.example.t_mobop_werewolf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val button_Firebase = findViewById<Button>(R.id.buttonFirebaseTest)

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
        button_Firebase.setOnClickListener {
            Log.d("MainActivity", "FIREBASE BUTTON CLIKED")
            setupGeneralDatalist()
            Toast.makeText(this, GeneralDataModel.getGeneralData("StoryState"), Toast.LENGTH_SHORT).show()
        }
    }

    fun setupGeneralDatalist(){
        val GeneralDataList = findViewById<ListView>(R.id.generalData_list)
        GeneralDataList.adapter = GeneralDataAdapter(this, GeneralDataModel.getAllData())
    }

    override fun update(o: Observable?, arg: Any?) {
        TODO("Not yet implemented")
    }

    // ---------x--------- Custom Firebase functions ---------x---------
        // Check if a room is open
            // Yes : join it and ask player pseudo
            // No : ask room name, host/player pseudo and take him to setup room

    class GeneralDataAdapter(context: Context, ListItems: ArrayList<String>): BaseAdapter()
    {
        private val mContext : Context = context
        //private val ListItems = arrayListOf<String>("Test1", "ttas")
        private var mList: ArrayList<String> = ListItems

        override fun getCount(): Int {return mList.size}
        override fun getItem(position: Int): Any {return "None"}
        override fun getItemId(position: Int): Long {return position.toLong()}

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View
        {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_general_data, viewGroup, false)
            val item = rowMain.findViewById<TextView>(R.id.row_generalData_item)
            Log.d("GeneralDataAdapter", "issue1")
            item.text = mList.toString()
            Log.d("GeneralDataAdapter", "Added item to list displayed")
            return rowMain
        }
    }

    fun checkOpenRoom(): Boolean{

        return false
    }

    fun JoinRoom(RoomName: String, Pseudo: String): Boolean{

        return false
    }

    fun OpenNewRoom(RoomName: String, NbPlayers: Int,HostName: String ): Boolean{

        return false
    }

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


