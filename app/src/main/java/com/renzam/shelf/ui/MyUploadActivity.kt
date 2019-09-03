package com.renzam.shelf.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.renzam.shelf.R
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyUploadActivity : AppCompatActivity() {

    lateinit var myList: ArrayList<String>
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_upload)



        myList = ArrayList()


        listview = findViewById(R.id.listView)

        val db = FirebaseFirestore.getInstance()


        val docRef = db.collection("shops").whereEqualTo("userId",FirebaseAuth.getInstance().uid)

        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            for (documentChange in querySnapshot!!.documentChanges) {


                var bussinessName: String = documentChange.document.data.get("businessName").toString()

                Log.i("For loop bussiness Name*********#####",bussinessName)
                myList.add(bussinessName)




                //var date: FieldValue = documentChange.document.data.get("date")
//                var dateFor: String = documentChange.document.data.get("date").toString()
//                Log.i("Time Date ****&&&&*****","Date $dateFor")
//                var date =  documentChange.document.data.get("date")


            }
            arrayAdapter =  ArrayAdapter(this,android.R.layout.simple_list_item_1,myList)
            listview.adapter = arrayAdapter
        }

    }





//        arrayAdapter =  ArrayAdapter(this,android.R.layout.simple_list_item_1,myList)
//        listview.adapter = arrayAdapter

//  }
}
