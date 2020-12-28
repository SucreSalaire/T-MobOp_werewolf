package com.example.t_mobop_werewolf.FirebaseData

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/*
    This object allows to communicate with the Firebase Realtime Database.
        Data is accessible in a local snapshot and is updated on any change (method onDataChange())
        The Database has a listener attached to it that allows to automatically update it.


    The following function are usable anywhere in the project to access a value from the
    database:

        GeneralDataModel.createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
            > Allows to open a new room, returns true if new roomed open

        GeneralDataModel.joinRoom(RoomName: String, Pseudo: String): Boolean
            > Adds a player to the room, returns true if added

        GeneralDataModel.getPlayersNumber(RoomName: String): Int
            > Returns the number of players in the room

        GeneralDataModel.getAnyData(Path: String): Any
            > Returns the requested data from the designated path

        GeneralDataModel.getStoryState(RoomName: String): Double
            > Returns the current state of the story

        GeneralDataModel.changeStoryState(RoomName: String, NextState: Double) : Boolean
            > Set the story state of the room

        GeneralDataModel.getPlayerRole(RoomName: String, PlayerPseudo: String): String
            > Get the role of the player

        GeneralDataModel.killPlayer(RoomName: String, PlayerPseudo: String): Boolean
            > Kill the player

        GeneralDataModel.
        GeneralDataModel.
        GeneralDataModel.

        GeneralDataModel.setupDatabaseAsDefault()
            > Reinitialize the database as default

 */

object GeneralDataModel: Observable()
{
    private var database = Firebase.database.reference
    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference}

    // Event listener attached to the database root
    private var mGeneralListener: ValueEventListener? = null

    // Local variables containing the database values
    lateinit var localSnapshot: DataSnapshot
    var localRoomName: String = "None"
    var localPseudo: String = "None"
    var localRole: String = "None"

    init
    {
        if (mGeneralListener != null) {getDatabaseRef()?.removeEventListener(mGeneralListener!!)}
        mGeneralListener = null

        mGeneralListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try
                {
                    if (snapshot != null)
                    {
                        localSnapshot = snapshot
                        Log.d("GeneralDataModel", "Data updated")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("GeneralDataModel", "Updated snapshot download cancelled!")}

        } // mValueDataListener
        getDatabaseRef()?.addValueEventListener(mGeneralListener!!)
    } // init



    // ---------x---------
    // Firebase Functions

    fun createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
    {
        Log.d("GeneralDataModel", "Fun createRoom() called")
        var roomAlreadyOpen: Boolean = false
        try
        {
            for (item: DataSnapshot in localSnapshot.child("Rooms").children){
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

                localRoomName = RoomName
                localPseudo = HostName
            }
        } catch (e: Exception) { e.printStackTrace() }
        return roomAlreadyOpen.not()
    }


    fun joinRoom(RoomName: String, Pseudo: String): Boolean
    {
        // Add check if player already exists
        // Add check for max players
        Log.d("GeneralDataModel", "Fun joinRoom() called")
        var joinSuccess: Boolean = false
        joinSuccess = try {
            val nbPlayer = getPlayersNumber(RoomName) + 1
            database.child("$RoomName/Players/Player$nbPlayer/Alive").setValue(true)
            database.child("$RoomName/Players/Player$nbPlayer/Pseudo").setValue(Pseudo)
            database.child("$RoomName/Players/Player$nbPlayer/Role").setValue("None")
            database.child("$RoomName/GeneralData/NbPlayers").setValue(nbPlayer)
            localRoomName = RoomName
            localPseudo = Pseudo
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GeneralDataModel", "Fun joinRoom() failed")
            false
        }
        return joinSuccess
    }


    fun setupAndStartGame(){

    }


    fun getPlayersNumber(RoomName: String): Long {
        return localSnapshot.child("$RoomName/GeneralData/NbPlayers").value as Long
    }

    fun getAnyData(Path: String): Any {
        return localSnapshot.child(Path).value as Any
    }

    fun getStoryState() : Double {
        localSnapshot.child("$localRoomName/GeneralData/StoryState").value as Double
        return 0.0
    }

    fun changeStoryState(NextState: Double) : Boolean {
        try{
            database.child("$localRoomName/GeneralData/StoryState").setValue(NextState)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GeneralDataModel", "fun changeStoryState failed")
            return false
        }
    }

    fun getPlayerRole(PlayerPseudo: String): String {
        try{
            return localSnapshot.child("$localRoomName/Players/$PlayerPseudo/Role").value as String
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GeneralDataModel", "fun killPlayer failed")
            return "Failed"
        }
    }

    fun killPlayer(PlayerPseudo: String): Boolean {
        // add role dependant kill count in DB
        try{
            database.child("$localRoomName/GeneralData/Players/$PlayerPseudo/Alive").setValue(false)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GeneralDataModel", "fun killPlayer failed")
            return false
        }
    }

    // Must be used only once !!! Otherwise will reinitialize all the game
    fun resetAllDatabase(){
        //database = Firebase.database.reference
        database.removeValue()              // removes everything at the root
        Log.d("GeneralDataModel", "Database has been cleared.")

        database.child("Rooms/Room0").setValue("Open")

        Log.d("GeneralDataModel", "Database has been set to default.")
    }


}