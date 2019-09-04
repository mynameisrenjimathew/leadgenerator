package com.renzam.shelf.data

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Pattern


class ViewModel : ViewModel() {


    @Bindable
    val bussinessName = MutableLiveData<String>()
    val ownerName = MutableLiveData<String>()
    val placeName = MutableLiveData<String>()
    val ownerPhoneNumber = MutableLiveData<String>()

    lateinit var catogoreyOfShop: String
    lateinit var contextofthisapp: Context
    lateinit var thisBitmap: Bitmap






    var latitude_: Double = 2.1000
    var longitude_: Double = 2.1000
    lateinit var urL: String

    var success = MutableLiveData<String>()

    fun getDatas() {


        if (catogoreyOfShop != "select Category") {
            if (bussinessName.value != null) {
                if (placeName.value != null) {
                    if (ownerName.value != null) {
                        if (ownerPhoneNumber.value != null && isValidMobile(ownerPhoneNumber.value.toString())) {
                            UploadDataToServer()
                        } else {
                            Toast.makeText(
                                contextofthisapp,
                                "Enter Owner Phone Number Or Check the Number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(contextofthisapp, "Please Enter Owner Name", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(contextofthisapp, "Please Enter Place Name", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(contextofthisapp, "Please Enter Bussiness Name", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(contextofthisapp,"Please Select Category",Toast.LENGTH_SHORT).show()

        }

    }

    @SuppressLint("LongLogTag")
    fun UploadDataToServer(){

//        Log.i(
//            "items*********** 8**8***",
//            "${bussinessName.value} ,${ownerName.value} ,${placeName.value} ,${ownerPhoneNumber.value}  ,$catogoreyOfShop  ,$latitude_  ,$longitude_ "
//        )
        success.value = ""

//        if (bussinessName.value != "" && ownerName.value != "" && placeName.value != "" && ownerPhoneNumber.value != "") {


            val storage = FirebaseStorage.getInstance()

            val storageRef = storage.reference

            val imageName = UUID.randomUUID().toString() + ".jpg"
            val imagesRef: StorageReference? = storageRef.child("images")

            val db = FirebaseFirestore.getInstance()

            val progressDialog = ProgressDialog(contextofthisapp)
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Please Wait.. :) ")
            progressDialog.show()

            val baos = ByteArrayOutputStream()
            thisBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            val data = baos.toByteArray()

            val child = imagesRef?.child(imageName)
            val uploadTask = child?.putBytes(data)

            uploadTask?.addOnFailureListener {

                Toast.makeText(contextofthisapp, "Sorry Uploaded Failed", Toast.LENGTH_SHORT).show()
                Log.e("Image Upload Error", "Sorry" + it.message)
                progressDialog.dismiss()

            }?.addOnSuccessListener {

                //Toast.makeText(contextofthisapp, "Upload Image to the database", Toast.LENGTH_SHORT).show()

                child.downloadUrl.addOnSuccessListener {

                   // Toast.makeText(contextofthisapp, "Url  :$it", Toast.LENGTH_SHORT).show()

                    urL = it.toString()

                    val shop = DataModels(
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        catogoreyOfShop,
                        bussinessName.value ?: "",
                        ownerName.value ?: "",
                        ownerPhoneNumber.value ?: "",
                        placeName.value ?: "",
                        urL,
                        latitude_,
                        longitude_,
                        Timestamp.now()
                    )


                    db.collection("shops")
                        .add(shop)
                        .addOnSuccessListener {

                            Toast.makeText(contextofthisapp, "great!! Good Job :) ", Toast.LENGTH_SHORT).show()
                            success.value = "success"
                            bussinessName.value = null
                            ownerName.value = null
                            ownerPhoneNumber.value = null
                            placeName.value = null
                            progressDialog.dismiss()


                        }.addOnFailureListener {
                            Toast.makeText(contextofthisapp, "Sorry ", Toast.LENGTH_SHORT).show()
                            Log.e("errorrr@@@********", "Sorry  ${it.message}")
                        }
                }

            }
//        }else {
//            Toast.makeText(contextofthisapp, "Please fill All Fields :)", Toast.LENGTH_SHORT).show()
//        }

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
    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length in 7..13
        } else false
    }


}