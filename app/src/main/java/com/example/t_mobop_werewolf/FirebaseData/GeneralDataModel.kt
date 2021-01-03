package com.example.t_mobop_werewolf.FirebaseData

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.t_mobop_werewolf.PlayingActivity
import com.example.t_mobop_werewolf.PlayingHostActivity
import com.example.t_mobop_werewolf.WaitingRoomActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.android.synthetic.main.activity_waiting_room.*
import java.util.*

/*
    This object allows to communicate with the Firebase Realtime Database.
        Data is accessible in a local snapshot and is updated on any change (method onDataChange())
        The Database has a listener attached to it that allows to automatically update it.

    The following functions are usable anywhere in the project to access/manipulate the database :

        GeneralDataModel.nextState(currentState: Long): Long
        GeneralDataModel.createRoom(RoomName: String, NbPlayers: Int, HostName: String ): Boolean
        GeneralDataModel.joinRoom(RoomName: String, Pseudo: String): Boolean
        GeneralDataModel.setupAndStartGame()
        GeneralDataModel.distributeRoles()
        GeneralDataModel.getPlayersNumber(RoomName: String): Long
        GeneralDataModel.getPlayersPseudos(RoomName: String): ArrayList<String>
        GeneralDataModel.getPlayersVotes(RoomName: String): ArrayList<Int>
        GeneralDataModel.getPlayerRole(PlayerPseudo: String): String
        GeneralDataModel.validateVote(RoomName: String, voteType: String): Boolean
        GeneralDataModel.getAnyData(Path: String): Any
        GeneralDataModel.setAnyData(Path: String, Value: Any): Boolean
        GeneralDataModel.getStoryState(RoomName: String): Double
        GeneralDataModel.changeStoryState(RoomName: String, NextState: Double) : Boolean
        GeneralDataModel.killPlayer(RoomName: String, PlayerPseudo: String): Boolean
        GeneralDataModel.savePlayer(PlayerPseudo: String): Boolean
        GeneralDataModel.localSnapshotInit()
        GeneralDataModel.setupDatabaseAsDefault()
 */

object GeneralDataModel: Observable()
{
    private var TAG = "GeneralDataModel"

    // Database references
    private var database = Firebase.database.reference
    private fun getDatabaseRef() : DatabaseReference? {return FirebaseDatabase.getInstance().reference}

    // Event listener attached to the database root
    private var mGeneralListener: ValueEventListener? = null

    // Local variables containing the database values
    lateinit var localSnapshot: DataSnapshot
    var localRoomName: String = "None"
    var localPseudo: String = "None"
    var localPlayerNb: Long = 1
    var localRole: String = "None"
    var iAmtheHost: Boolean = false

    // Database main listener
    init
    {
        if (mGeneralListener != null) {getDatabaseRef()?.removeEventListener(mGeneralListener!!)}
        mGeneralListener = null

        mGeneralListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    localSnapshot = snapshot
                    Log.d(TAG, "Database updated")
                    setChanged()
                    notifyObservers()
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "localSnapshot update cancelled!")}

        } // mValueDataListener
        getDatabaseRef()?.addValueEventListener(mGeneralListener!!)
    } // init




    //                                      ---------x---------
    //                                      Firebase Functions


    // This function is called at the end of a turn. It will decide the next state.
    fun nextState(currentState: Long): Long
    {
        Log.d(TAG, "fun nextState($currentState) called")
        var nextState : Long = 0
        var textToShow: String = "Waiting"

        when(currentState) {
            1.toLong() -> {   // Game starts
                textToShow = "2"
                nextState = 2 // Next state 2 by default, village goes to sleep
            }

            2.toLong() -> {   // Village sleeping
                textToShow = "3"
                nextState = 3 // Next state 3 by default, werewolf will play
            }

            3.toLong() -> {  // Werewolf have played
                if (localSnapshot.child("$localRoomName/RolesData/VillagerCount").value as Long == 0 as Long) {
                    textToShow = "9"
                    nextState = 9   // end of game, villagers are dead
                } else if (localSnapshot.child("$localRoomName/RolesData/WitchAlive").value as Boolean) {
                    textToShow = "4"
                    nextState = 4   // Witch will play
                } else if (localSnapshot.child("$localRoomName/RolesData/FortuneTellerAlive").value as Boolean) {
                    textToShow = "5"
                    nextState = 5   // FortuneTeller will play
                } else {
                    textToShow = "6"
                    nextState = 6   // Village will wake up
                }
            }

            4.toLong() -> {  // Witch has played
                if (localSnapshot.child("$localRoomName/RolesData/FortuneTellerAlive").value as Boolean) {
                    textToShow = "5"
                    nextState = 5 // FortuneTeller will play
                } else {
                    nextState = 6 // Village will wake up
                }
            }

            5.toLong() -> {  // Fortune teller has played
                textToShow = "6"
                nextState = 6 // Village will wake up
            }

            6.toLong() -> {  // Village has discovered the dead
                textToShow = "7"
                nextState = 7 // Village will barbecue someone
            }

            7.toLong() -> {  // Village has sacrificed
                textToShow = "8"
                nextState = 8 // Village will reveal sacrifice
            }

            8.toLong() -> {  // Village sacrifice revelation
                if (localSnapshot.child("$localRoomName/RolesData/WerewolvesCount").value as Long > 0) {
                    textToShow = "2"
                    nextState = 2   // going to sleep
                } else {
                    textToShow = "9"
                    nextState = 9   // end of game, werevolves are dead
                }
            }

            9.toLong() -> {  // Game end
                Log.d(TAG, "The game is finished and you should not see this text.")
            }
            else -> textToShow = "You messed up something."
        }

        changeStoryState(nextState)
        Log.d(TAG, "fun nextStage success")
        return nextState
    }


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
                database.child("$RoomName/GeneralData/StoryState").setValue(0) //(0.0)
                database.child("$RoomName/GeneralData/WaitingRoomOpen").setValue(false)
                database.child("$RoomName/GeneralData/Flag").setValue(false)

                database.child("$RoomName/Players/Player1/Alive").setValue(true)
                database.child("$RoomName/Players/Player1/Pseudo").setValue(HostName)
                database.child("$RoomName/Players/Player1/Role").setValue("None")
                database.child("$RoomName/Players/Player1/Voted").setValue(false)
                database.child("$RoomName/Players/Player1/Votes").setValue(0)
                database.child("$RoomName/Players/Player1/Werewolf").setValue(false)

                database.child("$RoomName/RolesData/PotionKill").setValue(1)
                database.child("$RoomName/RolesData/PotionSave").setValue(1)
                database.child("$RoomName/RolesData/VillagersCount").setValue(0)
                database.child("$RoomName/RolesData/WerewolvesCount").setValue(0)
                database.child("$RoomName/RolesData/WitchAlive").setValue(false)
                database.child("$RoomName/RolesData/FortuneTellerAlive").setValue(false)

                localRoomName = RoomName
                localPseudo = HostName
                iAmtheHost = true
            }
        } catch (e: Exception) { e.printStackTrace() }
        return roomAlreadyOpen.not()
    }


    fun joinRoom(RoomName: String, Pseudo: String) : Boolean
    {
        Log.d(TAG, "Fun joinRoom() called")
        var joinSuccess : Boolean = false
        if (localSnapshot.child("0_Rooms/$RoomName").value.toString() == "Open")
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
                localPlayerNb = nbPlayer
                // update players list waiting in the room
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
        Log.d(TAG, "Fun setupAndStartGame() called")
        database.child("0_Rooms/$localRoomName").setValue("Closed")
        try{
            distributeRoles()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun setupAndStartGame()/distributeRoles() failed")
        }
        database.child("$localRoomName/GeneralData/GameStarted").setValue(true)
        try{
            changeStoryState(1)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Fun setupAndStartGame()/changeStoryState(1) failed")
        }
    }

    fun distributeRoles()
    {
        Log.d(TAG, " fun ${getPlayersNumber(localRoomName)} called")
        when(getPlayersNumber(localRoomName))
        {
            3.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(3)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player3/Role").setValue("Villager")

                database.child("$localRoomName/Players/Player2/Werewolf").setValue(true)

                database.child("$localRoomName/RolesData/VillagersCount").setValue(2)
                database.child("$localRoomName/RolesData/WerewolvesCount").setValue(1)
                database.child("$localRoomName/RolesData/WitchAlive").setValue(false)
                database.child("$localRoomName/RolesData/FortuneTellerAlive").setValue(false)

                database.child("$localRoomName/GeneralData/RolesDistributed").setValue(true)
            }
            4.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(4)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player3/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player4/Role").setValue("Witch")

                database.child("$localRoomName/Players/Player3/Werewolf").setValue(true)

                database.child("$localRoomName/RolesData/VillagersCount").setValue(3)
                database.child("$localRoomName/RolesData/WerewolvesCount").setValue(1)
                database.child("$localRoomName/RolesData/WitchAlive").setValue(true)
                database.child("$localRoomName/RolesData/FortuneTellerAlive").setValue(false)

                database.child("$localRoomName/GeneralData/RolesDistributed").setValue(true)
            }
            5.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(5)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player3/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player4/Role").setValue("Witch")
                database.child("$localRoomName/Players/Player5/Role").setValue("FortuneTeller")

                database.child("$localRoomName/Players/Player2/Werewolf").setValue(true)
                database.child("$localRoomName/Players/Player3/Werewolf").setValue(true)

                database.child("$localRoomName/RolesData/VillagersCount").setValue(3)
                database.child("$localRoomName/RolesData/WerewolvesCount").setValue(2)
                database.child("$localRoomName/RolesData/WitchAlive").setValue(true)
                database.child("$localRoomName/RolesData/FortuneTellerAlive").setValue(true)

                database.child("$localRoomName/GeneralData/RolesDistributed").setValue(true)
            }
            6.toLong() ->
            {
                Log.d(TAG, "fun distributeRoles(6)")
                database.child("$localRoomName/Players/Player1/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player2/Role").setValue("Villager")
                database.child("$localRoomName/Players/Player3/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player4/Role").setValue("Werewolf")
                database.child("$localRoomName/Players/Player5/Role").setValue("Witch")
                database.child("$localRoomName/Players/Player6/Role").setValue("FortuneTeller")

                database.child("$localRoomName/Players/Player3/Werewolf").setValue(true)
                database.child("$localRoomName/Players/Player4/Werewolf").setValue(true)

                database.child("$localRoomName/RolesData/VillagersCount").setValue(4)
                database.child("$localRoomName/RolesData/WerewolvesCount").setValue(2)
                database.child("$localRoomName/RolesData/WitchAlive").setValue(true)
                database.child("$localRoomName/RolesData/FortuneTellerAlive").setValue(false)

                database.child("$localRoomName/GeneralData/RolesDistributed").setValue(true)
            }
            else ->
            {
                Log.d(TAG, "fun distributeRoles(): can't assign roles to players")
                database.child("$localRoomName/GeneralData/RolesDistributed").setValue(false)
            }
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


    fun getPlayersPseudos(RoomName: String): ArrayList<String> {
        var nbPlayers = getPlayersNumber(RoomName)
        var playersPseudoArray = ArrayList<String>()
        for (i in 1..nbPlayers)
        {
            if (localSnapshot.child("$RoomName/Players/Player$i/Pseudo").exists()){
                playersPseudoArray.add(localSnapshot.child("$RoomName/Players/Player$i/Pseudo").value as String)
            } else {
                Log.d(TAG, "fun getPlayersPseudo($nbPlayers) failed")
            }

        }
        return playersPseudoArray
    }


    fun getPlayersVotes(RoomName: String): ArrayList<Int> {
        var nbPlayers = getPlayersNumber(RoomName)
        var playersVotesArray = ArrayList<Int>()
        for (i in 1..nbPlayers) {
            if (localSnapshot.child("$RoomName/Players/Player$i/Votes").exists()) {
                playersVotesArray.add(localSnapshot.child("$RoomName/Players/Player$i/Votes").value as Int)
            } else {
                Log.d(TAG, "fun getPlayersVotes() failed")
            }
        }
        return playersVotesArray
    }


    fun getPlayerRole(PlayerPseudo: String): String {
        return try{
            localSnapshot.child("$localRoomName/Players/Player$localPlayerNb/Role").value as String
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun getPlayerRole failed")
            "Failed"
        }
    }


    fun validateVote(RoomName: String, voteType: String): Boolean {
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


    fun getStoryState() : Long {
        return localSnapshot.child("$localRoomName/GeneralData/StoryState").value as Long
    }


    fun changeStoryState(NextState: Long) : Boolean {
        return try{
            Log.d(TAG, "changeStoryState called")
            database.child("$localRoomName/GeneralData/StoryState").setValue(NextState)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun changeStoryState failed")
            false
        }
    }


    fun killPlayer(PlayerPseudo: String): Boolean {
        // add role dependant kill count in DB
        return try{
            database.child("$localRoomName/Players/$PlayerPseudo/Alive").setValue(false)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun killPlayer failed")
            false
        }
    }


    fun savePlayer(PlayerPseudo: String): Boolean {
        // add role dependant save count in DB
        return try{
            database.child("$localRoomName/Players/$PlayerPseudo/Alive").setValue(true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "fun savePlayer failed")
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


    fun resetAllDatabase(){
        //database = Firebase.database.reference
        database.removeValue()              // removes everything at the root
        Log.d(TAG, "Database has been cleared.")

        database.child("0_NbPhoneConnected").setValue(0)
        database.child("0_Rooms/Room0").setValue("Open")

        Log.d(TAG, "Database has been set to default.")
    }

}