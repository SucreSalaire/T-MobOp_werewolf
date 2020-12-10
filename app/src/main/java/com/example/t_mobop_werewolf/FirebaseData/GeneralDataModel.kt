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
    private var mDataList: ArrayList<GeneralData>? = ArrayList()

    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("GeneralData")
    }

    init
    {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }

        mValueDataListener = null
        Log.i("GeneralDataModel", "data init")

        mValueDataListener = object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                try
                {
                    Log.i("GeneralDataModel", "data updated")
                    val dataUpdated: ArrayList<GeneralData> = ArrayList()
                    val dataHash: HashMap<String,String> = HashMap<String,String>()

                    if (snapshot != null)
                    {
                        for (items: DataSnapshot in snapshot.children) {
                            try {
                                dataUpdated.add(GeneralData(items))
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                        mDataList = dataUpdated
                        Log.i("GeneralDataModel", "data updated, size: " + dataUpdated!!.size)
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onCancelled(error: DatabaseError) {
                if (error != null) {
                    Log.i("DataModel", "data upload canceled, error = $(error.message)")
                }
            }
        } // mValueDataListener

        getDatabaseRef()?.addValueEventListener(mValueDataListener!!)

    } // init

    fun getData(): ArrayList<GeneralData>?{ return mDataList }
}