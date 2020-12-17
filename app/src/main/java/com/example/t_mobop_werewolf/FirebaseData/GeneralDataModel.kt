package com.example.t_mobop_werewolf.FirebaseData

import android.util.Log
import android.widget.Toast
import com.example.t_mobop_werewolf.MainActivity
import com.google.firebase.database.*
import java.util.*
import kotlin.coroutines.coroutineContext


object GeneralDataModel: Observable()
{
    private var mValueDataListener: ValueEventListener? = null
    private var mDataList: ArrayList<String> = ArrayList()
    var list: ArrayList<String> = ArrayList()
    lateinit var item: String
    lateinit var AllSnap: DataSnapshot

    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("GeneralData")
    }

    init
    {
        if (mValueDataListener != null) {getDatabaseRef()?.removeEventListener(mValueDataListener!!)}

        mValueDataListener = null

        mValueDataListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try
                {
                    Log.d("GeneralDataModel", "GeneralData updated")
                    if (snapshot != null)
                    {
                        AllSnap = snapshot
                        list.clear()
                        mDataList.clear()
                        for (items: DataSnapshot in snapshot.children) {
                            try {
                                //list.add(GeneralData(snapshot.child("HostName"))) = THIS WORKS
                                item = items.value as String
                                list.add(item)
                                Log.d("GeneralDataModel", "Added value from snapshot")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.d("GeneralDataModel", "Can't take value from DB")}
                        }
                        mDataList = list
                        Log.d("GeneralDataModel", "Data updated")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {}
        } // mValueDataListener

        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)

    } // init

    fun getAllData() : ArrayList<String> { return mDataList }

    fun getGeneralData(Path: String): String{ return AllSnap.child(Path).value as String }

    //fun getGameStarted()
    //fun getHostName()
    //fun getRolesDistributed()
    //fun getRoomName()
    //fun getStoryState() : String { return AllSnap.child("StoryState").value as String }
    //fun getWaitRoomOpen()
}