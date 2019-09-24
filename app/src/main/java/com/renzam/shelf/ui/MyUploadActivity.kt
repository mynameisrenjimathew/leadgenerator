package com.renzam.shelf.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.renzam.shelf.R
import com.renzam.shelf.data.RecyClerModels
import com.renzam.shelf.data.RecyclerAdapter


class MyUploadActivity : AppCompatActivity() {

    lateinit var recycleView: RecyclerView
    lateinit var adapter: RecyclerAdapter
    lateinit var modelList: MutableList<RecyClerModels>
    lateinit var floatingActionButton: FloatingActionButton


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_upload)


        floatingActionButton = findViewById(R.id.floatingActonButtton)

        modelList = ArrayList()

        recycleView = findViewById(R.id.recycleView)

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("shops").whereEqualTo("userId", FirebaseAuth.getInstance().uid)
            .orderBy("date", Query.Direction.DESCENDING)


        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->


            for (documentChange in querySnapshot!!.documentChanges) {

                var bussinessName: String = documentChange.document.data.get("businessName").toString()
                var ownerName: String = documentChange.document.data.get("ownerName").toString()
                var url: String = documentChange.document.data.get("url").toString()
                var docId: String = documentChange.document.id
                var phoneNumber: String = documentChange.document.data.get("ownerPhoneNum").toString()
                var latitude: Double = documentChange.document.data.get("latitude") as Double
                var longitude: Double = documentChange.document.data.get("longitude") as Double

                var dateOf: Timestamp = documentChange.document.data.get("date") as Timestamp

                var dateString: String = android.text.format.DateFormat.format("dd-MM-yyyy", dateOf.toDate()) as String

                modelList.add(RecyClerModels(bussinessName, ownerName, url, docId, phoneNumber, latitude,longitude,dateString))

            }

            adapter = RecyclerAdapter(modelList, this)
            recycleView.adapter = adapter
            recycleView.layoutManager = LinearLayoutManager(this)



        }


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder

            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var mobileNum: String = modelList[viewHolder.adapterPosition].ownerPhoneNum
                Toast.makeText(this@MyUploadActivity, mobileNum, Toast.LENGTH_SHORT).show()

                dialContactPhone(mobileNum)


            }

        }).attachToRecyclerView(recycleView)


        floatingActionButton.setOnClickListener {
            startActivity(Intent(this,UploadActivity::class.java))
        }


    }

    private fun dialContactPhone(phoneNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)))
    }



}
