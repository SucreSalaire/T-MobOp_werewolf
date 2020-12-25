package com.example.t_mobop_werewolf.FirebaseData

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/*
    This object allows to communicate with the Firebase Realtime Database.
        Data is accessible either in a list or a Snapshot (type from Firebase).
        In this code, only the local snapshot will be used and accessed whenever a value is needed.
        The Database has a listener attached to it that allows to automatically update it with
        the method onDataChange().


    The following function are usable anywhere in the project to access a value from the
    database:

        GeneralDataModel.createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
            Allows to open a new room, returns true if new roomed open

        GeneralDataModel.joinRoom(RoomName: String, Pseudo: String): Boolean
            Adds a player to the room, returns true if added

        GeneralDataModel.getPlayersNumber(RoomName: String): Int
            Returns the number of players in the room

        GeneralDataModel.getAnyData(Path: String): Any
            Returns the requested data

        GeneralDataModel.getStoryState(RoomName: String): Double
            Returns the state of the story

        GeneralDataModel.setupDatabaseAsDefault()
            Reinitialize database as default

 */

object GeneralDataModel: Observable()
{
    // Event listener attached to the database
    private var mValueDataListener: ValueEventListener? = null

    // Local snapshot containing all the database
    lateinit var databaseSnapshot: DataSnapshot

    // List containing the values - not used - code is commented to avoid usage
    //private var mDataList: ArrayList<String> = ArrayList()
    //lateinit var item: String

    //private lateinit var database: DatabaseReference
    private var database = Firebase.database.reference
    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("GeneralData")}

    init
    {
        if (mValueDataListener != null) {getDatabaseRef()?.removeEventListener(mValueDataListener!!)}
        mValueDataListener = null

        mValueDataListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try
                {
                    if (snapshot != null)
                    {
                        databaseSnapshot = snapshot
                        Log.d("GeneralDataModel", "Data updated")
                        setChanged()
                        notifyObservers()

                        //mDataList.clear()
                        //for (items: DataSnapshot in snapshot.children) {
                        //    try {
                        //
                        //        item = items.value as String
                        //        mDataList.add(item)
                        //        Log.d("GeneralDataModel", "Added value from snapshot")
                        //    } catch (e: Exception) {
                        //        e.printStackTrace()
                        //        Log.d("GeneralDataModel", "Can't take value from DB")}
                        //}
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) { TODO("Handle errors ...") }

        } // mValueDataListener
        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)
    } // init


    // ---------x---------
    // The following functions allow access to the data stored into the Snapshot for the game

    fun createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
    {
        var roomAlreadyOpen: Boolean = false
        try
        {
            databaseSnapshot.child("Rooms").children.forEach { item: DataSnapshot ->
                Log.d("GeneralDataModel", item.value as String)
                if (item.value.toString() == RoomName) {
                    Log.d("GeneralDataModel", "Room already exists")
                    roomAlreadyOpen = true
                }
            }
            if (roomAlreadyOpen.not()){
                Log.d("GeneralDataModel", "Creating new room: $RoomName")
                database.child("Rooms/$RoomName").setValue("Open")
                database.child("$RoomName/GeneralData/GameStarted").setValue(false)
                database.child("$RoomName/GeneralData/HostName").setValue(HostName)
                database.child("$RoomName/GeneralData/MaxPlayers").setValue(NbPlayers)
                database.child("$RoomName/GeneralData/NbPlayers").setValue(1)
                database.child("$RoomName/GeneralData/RolesDistributed").setValue(false)
                database.child("$RoomName/GeneralData/RoomName").setValue(RoomName)
                database.child("$RoomName/GeneralData/StoryState").setValue(0.0)
                database.child("$RoomName/GeneralData/WaitingRoomOpen").setValue(false)

                database.child("$RoomName/Players/Player1/Alive").setValue(true)
                database.child("$RoomName/Players/Player1/Pseudo").setValue(HostName)
                database.child("$RoomName/Players/Player1/Role").setValue("None")
            }
        } catch (e: Exception) { e.printStackTrace() }
        return roomAlreadyOpen.not()
    }


    fun joinRoom(RoomName: String, Pseudo: String): Boolean
    {
        var joignedSuccess: Boolean = false
        databaseSnapshot.children.forEach { item: DataSnapshot ->
            Log.d("GeneralDataModel", item.value.toString())
            if (item.value.toString() == RoomName){
                // Also check if the same player doesn't exist
                Log.d("GeneralDataModel", "Room $RoomName joined.")
                val nbPlayers = databaseSnapshot.child("$RoomName/GeneralData/NbPlayers").value as Int
                database.child("Rooms/$RoomName/GeneralData/NbPlayers").setValue(nbPlayers + 1)
                joignedSuccess = true
            }
        }
        return joignedSuccess
    }


    fun getPlayersNumber(RoomName: String): Int{
        return 0
    }


    fun getAnyData(Path: String): Any{ return databaseSnapshot.child(Path).value as Any }


    fun getStoryState(RoomName: String) : Double {
        //return AllSnap.child("StoryState").value as String
        return 0.0
    }


    // Must be used only once !!! Otherwise will reinitialize all the game
    fun setupDatabaseAsDefault(){
        //database = Firebase.database.reference
        database.removeValue()              // removes everything at the root
        Log.d("GeneralDataModel", "Database has been cleared.")

        database.child("Rooms/Room0").setValue("Open")

        Log.d("GeneralDataModel", "Database has been set to default.")
    }


    // ---------x--------- DUMP CODE  ---------x---------

    //database.child("GeneralData").child("RoomName").setValue("None")
    //database.child("GeneralData").child("StoryState").setValue("0.0")
    //database.child("GeneralData").child("HostName").setValue("None")
    //database.child("GeneralData").child("WaitRoomOpen").setValue("false")
    //database.child("GeneralData").child("RolesDistributed").setValue("false")
    //database.child("GeneralData").child("GameStarted").setValue("false")

    //database.child("RolesData").child("RevivePotions").setValue("1")
    //database.child("RolesData").child("KillPotions").setValue("1")

    //database.child("Players").child("Model").child("Pseudo").setValue("Don't touch")
    //database.child("Players").child("Model").child("Alive").setValue("true")
    //database.child("Players").child("Model").child("Role").setValue("Villager")


    //fun getAllData() : ArrayList<String> { return mDataList }     can be used with ListAdapter
}