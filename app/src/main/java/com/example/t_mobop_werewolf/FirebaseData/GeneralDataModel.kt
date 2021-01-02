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




    //                                      ---------x---------
    //                                      Firebase Functions



    fun createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
    {
        Log.d(TAG, "Fun createRoom() called")
        var roomAlreadyOpen: Boolean = false
        try
        {
            for (item: DataSnapshot in localSnapshot.child("Rooms").children){
                Log.d(TAG, "ExistingRooms: ${item.key.toString()}")
                if (item.key.toString() == RoomName) {
                    Log.d(TAG, "Room already exists")
                    roomAlreadyOpen = true
                }
            }
            if (roomAlreadyOpen.not()){
                Log.d(TAG, "Creating new room: $RoomName")
                database.child("0_Rooms/$RoomName").setValue("Open")
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
                database.child("$RoomName/Players/Player1/Voted").setValue(false)
                database.child("$RoomName/Players/Player1/Votes").setValue(0)
                database.child("$RoomName/Players/Player1/Werewolf").setValue(false)

                localRoomName = RoomName
                localPseudo = HostName
                iAmtheHost = true
            }
        } catch (e: Exception) { e.printStackTrace() }
        return roomAlreadyOpen.not()
    }


    fun joinRoom(RoomName: String, Pseudo: String) : Boolean{
        // Add check if player already exists
        // Add check for max players
        Log.d(TAG, "Fun joinRoom() called")
        var joinSuccess : Boolean = false
        if (localSnapshot.child("Rooms/$RoomName").value.toString() == "Open")
        {
            try
            {
                val nbPlayer = getPlayersNumber(RoomName) + 1
                database.child("$RoomName/Players/Player$nbPlayer/Alive").setValue(true)
                database.child("$RoomName/Players/Player$nbPlayer/Pseudo").setValue(Pseudo)
                database.child("$RoomName/Players/Player$nbPlayer/Role").setValue("None")
                database.child("$RoomName/Players/Player$nbPlayer/Voted").setValue(false)
                database.child("$RoomName/Players/Player$nbPlayer/Votes").setValue(0)
                database.child("$RoomName/Players/Player$nbPlayer/Werewolf").setValue(false)
                database.child("$RoomName/GeneralData/NbPlayers").setValue(nbPlayer)
                localRoomName = RoomName
                localPseudo = Pseudo
                iAmtheHost = false
                Log.d(TAG, "Fun joinRoom() success")
                joinSuccess = true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "Fun joinRoom() failed")
                joinSuccess = false
            }
        } else {
            Log.d(TAG, "Room: $RoomName is closed, sorry.")
            joinSuccess = false
        }
        return joinSuccess
    }


    fun setupAndStartGame()
    {
        Log.d(TAG, "Fun setupAndStartGame()")
        //database.child("Rooms/$localRoomName").setValue("Closed")

        // Here can be added all the code necessary to configure special rules or any other
        // parameters related to the gameplay

        try{
            distributeRoles()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun setupAndStartGame()/distributeRoles() failed")
        }
        try{
            changeStoryState(1.0)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun setupAndStartGame()/changeStoryState(1.0) failed")
        }
    }

    private fun distributeRoles()
    {
        Log.d(TAG, getPlayersNumber(localRoomName).toString())
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
            else -> Log.d(TAG, "fun distributeRoles(): wrong number of players")
        }
    }

    fun getPlayersNumber(RoomName: String): Long {
        val value: Long
        if (localSnapshot.child("$RoomName/GeneralData/NbPlayers").exists()){
            value = localSnapshot.child("$RoomName/GeneralData/NbPlayers").value as Long
        } else {
            value = 1 //0(testing)
        }
        return value
    }

    fun getPlayersPseudos(RoomName: String): ArrayList<String>{
        var nbPlayers = getPlayersNumber(RoomName)
        var playersPseudoArray = ArrayList<String>()
        for (i in 1..nbPlayers)
        {
            if (localSnapshot.child("$RoomName/Players/Player${i.toString()}/Pseudo").exists()){
                playersPseudoArray.add(localSnapshot.child("$RoomName/Players/Player${i.toString()}/Pseudo").value as String)
            } else {
                Log.d(TAG, "fun getPlayersPseudo() failed")
            }

        }
        return playersPseudoArray
    }

    fun getPlayersVotes(RoomName: String): ArrayList<Int>{
        var nbPlayers = getPlayersNumber(RoomName)
        var playersVotesArray = ArrayList<Int>()
        for (i in 1..nbPlayers)
        {
            playersVotesArray.add(localSnapshot.child("$RoomName/Players/Player$i/Votes").value as Int)
        }
        return playersVotesArray
    }

    fun validateVote(RoomName: String, voteType: String  ): Boolean{
        var nbPlayers = getPlayersNumber(RoomName)
        var voteFlag: Boolean = true// set le flag a true
        when (voteType){
            "Villager" ->
                for (i in 1..nbPlayers) // check all flags
                {
                    if (!(localSnapshot.child("$RoomName/Players/Player$i/Voted").value as Boolean)) voteFlag = false
                }
            "Werewolf" ->
                for (i in 1..nbPlayers) // check all flags
                {
                    if (!(localSnapshot.child("$RoomName/Players/Player$i/Voted").value as Boolean)
                        && localSnapshot.child("$RoomName/Players/Player$i/Werewolf").value as Boolean ) voteFlag = false
                }
        }
        return voteFlag
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
        return try{
            database.child("$localRoomName/GeneralData/StoryState").setValue(NextState)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun changeStoryState failed")
            false
        }
    }

    fun getPlayerRole(PlayerPseudo: String): String {
        return try{
            localSnapshot.child("$localRoomName/Players/$PlayerPseudo/Role").value as String
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun getPlayerRole failed")
            "Failed"
        }
    }

    fun killPlayer(PlayerPseudo: String): Boolean {
        // add role dependant kill count in DB
        return try{
            database.child("$localRoomName/GeneralData/Players/$PlayerPseudo/Alive").setValue(false)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun killPlayer failed")
            false
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

        database.child("0_NbPhoneConnected").setValue(0)
        database.child("0_Rooms/Room0").setValue("Open")

        Log.d(TAG, "Database has been set to default.")
    }

}