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
    var bussinessName: String = ""
    var ownerName: String = ""
    var placeName: String = ""
    var ownerPhoneNumber: String = ""

    lateinit var catogoreyOfShop: String
    lateinit var contextofthisapp: Context
    lateinit var thisBitmap: Bitmap




    var latitude_: Double =-34.0
    var longitude_: Double =151.0
    lateinit var urL: String

    var success = MutableLiveData<String>()

    @SuppressLint("LongLogTag")
    fun getDatas() {


        if (catogoreyOfShop == "select Category") {

            Toast.makeText(contextofthisapp, "Please Select Catagory", Toast.LENGTH_SHORT).show()

        }else if (bussinessName.trim() == "") {

            Toast.makeText(contextofthisapp, "Please Enter Business Name", Toast.LENGTH_SHORT).show()

        }else if (placeName.trim() == "") {
            Toast.makeText(contextofthisapp, "Please Enter Place Name", Toast.LENGTH_SHORT).show()


        }else if (ownerName.trim()== "") {
            Toast.makeText(contextofthisapp, "Please Enter Owner Name", Toast.LENGTH_SHORT).show()

        }else if (ownerPhoneNumber.trim() == ""){
            Toast.makeText(contextofthisapp, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()

        }else if (!isValidMobile(ownerPhoneNumber)){

            Toast.makeText(contextofthisapp, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show()
        }else{


            Log.i("Datass ***********","$bussinessName  $placeName   $ownerName  $ownerPhoneNumber")


            UploadDataToServer()
        }



    }

    @SuppressLint("LongLogTag")
    fun UploadDataToServer(){

        success.value = ""

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

                            Toast.makeText(contextofthisapp, "great!! Good Job :) ", Toast.LENGTH_SHORT).show()
                            success.value = "success"
                            bussinessName = ""
                            ownerName = ""
                            ownerPhoneNumber = ""
                            placeName = ""
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
    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length in 7..13
        } else false
    }


}