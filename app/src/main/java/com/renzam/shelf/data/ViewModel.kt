package com.renzam.shelf.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.renzam.shelf.ui.MyUploadActivity
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewModel(application: Application) : AndroidViewModel(application) {


    @Bindable
    var bussinessName: String = ""
    var ownerName: String = ""
    var placeName: String = ""
    var ownerPhoneNumber: String = ""

    lateinit var catogoreyOfShop: String

    lateinit var thisImageUri: Uri

    val context = application

    var latitude_: Double = -34.0
    var longitude_: Double = 151.0
    lateinit var urL: String

    var success = MutableLiveData<String>()

    val db = FirebaseFirestore.getInstance()

    var fbusinessname = MutableLiveData<String>()
    var fplaceName = MutableLiveData<String>()
    var fownerName = MutableLiveData<String>()
    var fphoneNumber = MutableLiveData<String>()

    var isImage: Boolean = false


    @SuppressLint("LongLogTag")
    fun getDatas() {

        fbusinessname.value = "no"
        fplaceName.value = "no"
        fownerName.value = "no"
        fphoneNumber.value = "no"




        if (catogoreyOfShop == "Select Category") {

            Toast.makeText(context, "Please Select Category", Toast.LENGTH_SHORT).show()

        } else if (bussinessName.trim() == "") {

            Toast.makeText(context, "Please Enter Business Name", Toast.LENGTH_SHORT).show()
            fbusinessname.value = "Failed"

        } else if (placeName.trim() == "") {
            Toast.makeText(context, "Please Enter Place Name", Toast.LENGTH_SHORT).show()
            fplaceName.value = "Failed"

        } else if (ownerName.trim() == "") {
            Toast.makeText(context, "Please Enter Owner Name", Toast.LENGTH_SHORT).show()
            fownerName.value = "Failed"

        } else if (ownerPhoneNumber.trim() == "") {
            Toast.makeText(context, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()
            fphoneNumber.value = "Failed"

        } else if (!isValidMobile(ownerPhoneNumber)) {

            Toast.makeText(context, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show()
            fphoneNumber.value = "Failed"
        } else {


            Log.i("Datass ***********", "$bussinessName  $placeName   $ownerName  $ownerPhoneNumber")


                UploadDataToServer()


        }


    }

    @SuppressLint("LongLogTag")
    fun UploadDataToServer() {


        if (isImage) {


            success.value = "on"

            val storage = FirebaseStorage.getInstance()

            val storageRef = storage.reference

            val imageName = UUID.randomUUID().toString() + ".jpg"
            val imagesRef: StorageReference? = storageRef.child("images")


            var thisBitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, thisImageUri)

            val baos = ByteArrayOutputStream()
            thisBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            val data = baos.toByteArray()

            val child = imagesRef?.child(imageName)
            val uploadTask = child?.putBytes(data)

            uploadTask?.addOnFailureListener {

                Toast.makeText(context, "Sorry Uploaded Failed", Toast.LENGTH_SHORT).show()
                Log.e("Image Upload Error", "Sorry" + it.message)
//            progressDialog.dismiss()

            }?.addOnSuccessListener {


                child.downloadUrl.addOnSuccessListener {

                    // Toast.makeText(contextofthisapp, "Url  :$it", Toast.LENGTH_SHORT).show()

                    urL = it.toString()

                    val shop = DataModels(
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        catogoreyOfShop,
                        bussinessName,
                        ownerName,
                        ownerPhoneNumber,
                        placeName,
                        urL,
                        latitude_,
                        longitude_,
                        Timestamp.now()
                    )


                    db.collection("shops")
                        .add(shop)
                        .addOnSuccessListener {

                            Toast.makeText(context, "great!! Good Job :) ", Toast.LENGTH_SHORT).show()
                            success.value = "success"
                            bussinessName = ""
                            ownerName = ""
                            ownerPhoneNumber = ""
                            placeName = ""


                        }.addOnFailureListener {
                            Toast.makeText(context, "Sorry ", Toast.LENGTH_SHORT).show()
                            Log.e("errorrr@@@********", "Sorry  ${it.message}")
                        }
                }

            }
        } else {

            success.value = "on"
            val shop = DataModels(
                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                catogoreyOfShop,
                bussinessName,
                ownerName,
                ownerPhoneNumber,
                placeName,
                "https://firebasestorage.googleapis.com/v0/b/shelf-a7d22.appspot.com/o/default%2Fnoimageavail.jpg?alt=media&token=88a35a82-b417-488e-b693-06b725d8db51",
                latitude_,
                longitude_,
                Timestamp.now()
            )


            db.collection("shops")
                .add(shop)
                .addOnSuccessListener {

                    Toast.makeText(context, "great!! Good Job :) ", Toast.LENGTH_SHORT).show()

                    success.value = "success"
                    bussinessName = ""
                    ownerName = ""
                    ownerPhoneNumber = ""
                    placeName = ""



                }.addOnFailureListener {
                    Toast.makeText(context, "Sorry ", Toast.LENGTH_SHORT).show()
                    Log.e("errorrr@@@********", "Sorry  ${it.message}")
                }
        }


    }


    fun getCatagorey(category: String) {

        catogoreyOfShop = category

    }


    fun getImage(data: Intent?) {

        if (data != null){


            thisImageUri = data.data
            isImage = true

        }else{

            isImage = false
        }



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