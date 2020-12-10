package com.example.t_mobop_werewolf.FirebaseData

import com.google.firebase.database.DataSnapshot



class GeneralData (snapshot: DataSnapshot){
    lateinit var id: String
    lateinit var GameStarted: String
    lateinit var HostName: String
    lateinit var RolesDistributed: String
    lateinit var RoomName: String
    lateinit var StoryState: String
    lateinit var WaitRoomOpen: String

    init
    {
        try{
            val data: HashMap<String,Any> = snapshot.value as HashMap<String,Any>
            id = snapshot.key ?: ""
            GameStarted         = data["GameStarted"] as String
            HostName            = data["HostName"] as String
            RolesDistributed    = data["RolesDistributed"] as String
            RoomName            = data["RoomName"] as String
            StoryState          = data["StoryState"] as String
            WaitRoomOpen        = data["WaitRoomOpen"] as String
        } catch (e: Exception) { e.printStackTrace() }


    }

}