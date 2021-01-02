package com.example.t_mobop_werewolf.FirebaseData

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.t_mobop_werewolf.PlayingActivity
import com.example.t_mobop_werewolf.WaitingRoomActivity
import com.google.android.material.tabs.TabLayout
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
        GeneralDataModel.joinRoom(RoomName: String, Pseudo: String): Boolean
        GeneralDataModel.setupAndStartGame()
        GeneralDataModel.getPlayersNumber(RoomName: String): Int
        GeneralDataModel.getAnyData(Path: String): Any
        GeneralDataModel.getStoryState(RoomName: String): Double
        GeneralDataModel.changeStoryState(RoomName: String, NextState: Double) : Boolean
        GeneralDataModel.getPlayerRole(RoomName: String, PlayerPseudo: String): String
        GeneralDataModel.killPlayer(RoomName: String, PlayerPseudo: String): Boolean
        GeneralDataModel.localSnapshotInit()
        GeneralDataModel.setupDatabaseAsDefault()


 */

object GeneralDataModel: Observable()
{
    private var TAG = "GeneralDataModel"
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
    var iAmtheHost: Boolean = false

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
                        Log.d(TAG, "Data updated")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Updated snapshot download cancelled!")}

        } // mValueDataListener
        getDatabaseRef()?.addValueEventListener(mGeneralListener!!)
    } // init



    // ---------x---------
    // Firebase Functions

    fun createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
    {
        Log.d(TAG, "Fun createRoom() called")
        var roomAlreadyOpen: Boolean = false
        try
        {
            for (item: DataSnapshot in localSnapshot.child("Rooms").children){
                Log.d(TAG, item.value as String)
                if (item.value.toString() == RoomName) {
                    Log.d(TAG, "Room already exists")
                    roomAlreadyOpen = true
                }
            }
            if (roomAlreadyOpen.not()){
                Log.d(TAG, "Creating new room: $RoomName")
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
                iAmtheHost = true
            }
        } catch (e: Exception) { e.printStackTrace() }
        return roomAlreadyOpen.not()
    }


    fun joinRoom(RoomName: String, Pseudo: String): Boolean
    {
        // Add check if player already exists
        // Add check for max players
        Log.d(TAG, "Fun joinRoom() called")
        var joinSuccess: Boolean = false
        joinSuccess = try {
            val nbPlayer = getPlayersNumber(RoomName) + 1
            database.child("$RoomName/Players/Player$nbPlayer/Alive").setValue(true)
            database.child("$RoomName/Players/Player$nbPlayer/Pseudo").setValue(Pseudo)
            database.child("$RoomName/Players/Player$nbPlayer/Role").setValue("None")
            database.child("$RoomName/GeneralData/NbPlayers").setValue(nbPlayer)
            localRoomName = RoomName
            localPseudo = Pseudo
            iAmtheHost = false
            Log.d(TAG, "Fun joinRoom() success")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun joinRoom() failed")
            false
        }
        return joinSuccess
    }


    fun setupAndStartGame()
    {
        Log.d(TAG, "Fun setupAndStartGame()")
        database.child("Rooms/$localRoomName").setValue("Closed")
        // Here can be added all the code necessary to configure special rules or any other
        // parameters related to the gameplay

        distributeRoles()
        changeStoryState(1.0)
        // add a branch with only werewolf
    }

    fun distributeRoles()
    {
        when(getPlayersNumber(localRoomName))
        {
            3.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(3)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player3/Role").setValue("Witch")
            }
            4.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(4)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player3/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player4/Role").setValue("Witch")
            }
            5.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(5)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player3/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player4/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player5/Role").setValue("Witch")
            }
            6.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(6)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player3/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player4/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player5/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player6/Role").setValue("Witch")
            }
            else -> Log.d(TAG, "fun distributeRoles(): too much players")
        }
    }

    fun getPlayersNumber(RoomName: String): Long {
        return localSnapshot.child("$RoomName/GeneralData/NbPlayers").value as Long
    }

    fun getPlayersPseudos(RoomName: String): ArrayList<String>{
        var nbPlayers = getPlayersNumber(RoomName)
        var playersPseudoArray = ArrayList<String>()
        for (i in 1..nbPlayers)
        {
            playersPseudoArray.add(localSnapshot.child("$RoomName/Players/Player${i.toString()}/Pseudo").value as String)
        }
        return playersPseudoArray
    }

    fun validateVote(Type: String, ): Boolean{
        // set le flag a true
        // check all flags
            // if all are true (everybody has voted), return true
    }

    fun getAnyData(Path: String): Any {
        return localSnapshot.child(Path).value as Any
    }

    fun setAnyData(Path: String, Value: Any): Boolean {
        Log.d(TAG, "Fun setAnyData() called")
        var success: Boolean = false
        success = try{
            database.child(Path).setValue(Value)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun setAnyData() failed")
            false
        }
        return success
    }

    fun getStoryState() : Double {
        return localSnapshot.child("$localRoomName/GeneralData/StoryState").value as Double
    }

    fun changeStoryState(NextState: Double) : Boolean {
        try{
            database.child("$localRoomName/GeneralData/StoryState").setValue(NextState)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun changeStoryState failed")
            return false
        }
    }

    fun getPlayerRole(PlayerPseudo: String): String {
        try{
            return localSnapshot.child("$localRoomName/Players/$PlayerPseudo/Role").value as String
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun getPlayerRole failed")
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
            Log.d(TAG, "fun killPlayer failed")
            return false
        }
    }

    fun localSnapshotInit() {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(
            object: ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        localSnapshot = snapshot
                        Log.d(TAG, "localSnapshot single updated at startup")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Fun singleRead() cancelled")
                }
            }
        )
    }

    // Must be used only once !!! Otherwise will reinitialize all the game
    fun resetAllDatabase(){
        //database = Firebase.database.reference
        database.removeValue()              // removes everything at the root
        Log.d(TAG, "Database has been cleared.")

        database.child("NbPhoneConnected").setValue(0)
        database.child("Rooms/Room0").setValue("Open")

        Log.d(TAG, "Database has been set to default.")
    }

}