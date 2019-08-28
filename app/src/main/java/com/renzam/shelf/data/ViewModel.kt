package com.renzam.shelf.data

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.renzam.shelf.ui.UploadActivity
import java.io.ByteArrayOutputStream
import java.util.*

class ViewModel : ViewModel() {


    @Bindable
    val bussinessName = ObservableField<String>()
    val ownerName = ObservableField<String>()
    val placeName = ObservableField<String>()
    val ownerPhoneNumber = ObservableField<String>()


    lateinit var catogoreyOfShop: String
    lateinit var contextofthisapp: Context
    lateinit var thisBitmap: Bitmap

    var latitude_: Double = 2.1000
    var longitude_: Double = 2.1000
    lateinit var urL: String


    fun getDatas() {


        Log.i(
            "items*********** 8**8***",
            "${bussinessName.get()} ,${ownerName.get()} ,${placeName.get()} ,${ownerPhoneNumber.get()}  ,$catogoreyOfShop  ,$latitude_  ,$longitude_ "
        )

        //Toast.makeText(contextofthisapp, "something", Toast.LENGTH_SHORT).show()

        //repository.UploadToDatabase("","","")

        // repository.UploadToDatabase(catogoreyOfShop,bussinessName_,placeName_,ownerName_,phoneNumber_,thisBitmap,latitude_,longitude_)

        var storage = FirebaseStorage.getInstance()

        var storageRef = storage.reference

        val imageName = UUID.randomUUID().toString() + ".jpg"
        var imagesRef: StorageReference? = storageRef.child("images")

        val db = FirebaseFirestore.getInstance()


        val progressDialog = ProgressDialog(contextofthisapp)
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Please Wait.. :) ")
        progressDialog.show()

        val baos = ByteArrayOutputStream()
        thisBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val child = imagesRef?.child(imageName)
        val uploadTask = child?.putBytes(data)

        uploadTask?.addOnFailureListener {

            Toast.makeText(contextofthisapp, "Sorry Uploaded Failed", Toast.LENGTH_SHORT).show()
            Log.e("Image Upload Error", "Sorry" + it.message)
            progressDialog.dismiss()


        }?.addOnSuccessListener {

            Toast.makeText(contextofthisapp, "Upload Image to the database", Toast.LENGTH_SHORT).show()

            child.downloadUrl.addOnSuccessListener {


                Toast.makeText(contextofthisapp, "Url  :" + it.toString(), Toast.LENGTH_SHORT).show()
                urL = it.toString()

                var shop = DataModels(
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    catogoreyOfShop,
                    bussinessName.get() ?: "",
                    ownerName.get() ?: "",
                    ownerPhoneNumber.get() ?: "",
                    placeName.get() ?: "",
                    urL,
                    latitude_,
                    longitude_
                )



                db.collection("shops")
                    .add(shop)
                    .addOnSuccessListener {

                        Toast.makeText(contextofthisapp, "great ", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()

                    }.addOnFailureListener {
                        Toast.makeText(contextofthisapp, "Sorry ", Toast.LENGTH_SHORT).show()
                        Log.e("errorrr@@@********", "Sorry  ${it.message}")
                    }
            }

        }

    }

    fun getCatagorey(category: String) {

        catogoreyOfShop = category

    }

    fun Goodnews(context: Context) {

        contextofthisapp = context

    }

    fun getBitmap(bitmap: Bitmap) {

        thisBitmap = bitmap

    }

    fun getLocation(latitude: Double, longitude: Double) {

        latitude_ = latitude
        longitude_ = longitude

    }

}
