package com.example.t_mobop_werewolf.FirebaseData

import android.util.Log
import com.example.t_mobop_werewolf.PlayingActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*




// OBJECT NOT USED !!!!
// NAGI     28 dec 2020
// SINGLE LISTENER ADDED DIRECTLY TO PLAYING ACTIVITY




object StoryState
{

    private var roomName = GeneralDataModel.localRoomName

    var storyState: Double = 0.0

    private fun getStoryStateRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("$roomName/GeneralData/StoryState")
    }

    private var mStoryStateListener: ValueEventListener? = null

    init
    {
        if (mStoryStateListener != null) {
            getStoryStateRef()?.removeEventListener(mStoryStateListener!!)}
        mStoryStateListener = null

        mStoryStateListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try
                {
                    if (snapshot != null)
                    {
                        storyState = snapshot.value as Double
                        Log.d("StoryState", "Data updated")
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("StoryState", "Updated snapshot download cancelled!")}

        } // mValueDataListener
        getStoryStateRef()?.addValueEventListener( mStoryStateListener!!)
    } // init
}