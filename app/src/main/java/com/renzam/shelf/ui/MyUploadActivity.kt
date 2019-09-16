package com.renzam.shelf.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.renzam.shelf.R
import com.renzam.shelf.data.RecyClerModels
import com.renzam.shelf.data.RecyclerAdapter
import java.lang.Exception


class MyUploadActivity : AppCompatActivity() {

    lateinit var recycleView: RecyclerView
    lateinit var adapter: RecyclerAdapter
    lateinit var modelList: MutableList<RecyClerModels>


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_upload)


        modelList = ArrayList()

        recycleView = findViewById(R.id.recycleView)

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("shops").whereEqualTo("userId", FirebaseAuth.getInstance().uid)

        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            for (documentChange in querySnapshot!!.documentChanges) {

                var bussinessName: String = documentChange.document.data.get("businessName").toString()
                var ownerName: String = documentChange.document.data.get("ownerName").toString()
                var url: String = documentChange.document.data.get("url").toString()
                var docId: String = documentChange.document.id
                var phoneNumber: String = documentChange.document.data.get("ownerPhoneNum").toString()
                Log.i("docid*************", docId)
                try {
                    var dateOf: Timestamp = documentChange.document.data.get("date") as Timestamp
                    //Log.i("For loop bussiness Name*********#####", bussinessName)

                    Log.i("csac********", bussinessName)
                    //myList.add("$bussinessName - ${dateOf.toDate()}")

                    modelList.add(RecyClerModels(bussinessName, ownerName, url, docId , phoneNumber))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("Error*****", e.message)
                }


            }

            adapter = RecyclerAdapter(modelList, this)
            recycleView.adapter = adapter
            recycleView.layoutManager = LinearLayoutManager(this)

        }



        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder


            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var mobileNum: String =  modelList[viewHolder.adapterPosition].ownerPhoneNum
                Toast.makeText(this@MyUploadActivity,mobileNum,Toast.LENGTH_SHORT).show()

                dialContactPhone(mobileNum)


            }

        }).attachToRecyclerView(recycleView)

    }
    private fun dialContactPhone(phoneNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)))
    }


}
