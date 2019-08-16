package com.renzam.shelf.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.renzam.shelf.R
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    lateinit var bussinesNameEditText: EditText
    lateinit var ownerNameEditText: EditText
    lateinit var placeNameEdittext: EditText
    lateinit var uploadButton: Button
    lateinit var uploadImageView: ImageView
    lateinit var spinCategory: Spinner
    lateinit var ownerPhoenNumberEditText: EditText
    lateinit var catogoreyList: ArrayList<String>
    lateinit var locationManager: LocationManager
    lateinit var location: Location

    lateinit var bitmap: Bitmap

    var storage = FirebaseStorage.getInstance()

    var storageRef = storage.reference

    val imageName = UUID.randomUUID().toString() + ".jpg"
    var imagesRef: StorageReference? = storageRef.child("images")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        if (!Companion.isOnline(this)) {
            Toast.makeText(this,"Turn On Internet Connection",Toast.LENGTH_LONG).show()
        }
        
        catogoreyList = ArrayList()
        catogoreyList.add("select Catogorey")
        catogoreyList.add("department store")
        catogoreyList.add("supermarket")
        catogoreyList.add("grocer")
        catogoreyList.add("greengrocer")
        catogoreyList.add("bookShop")
        catogoreyList.add("stationary")
        catogoreyList.add("clothes shop")
        catogoreyList.add("optican")
        catogoreyList.add("petshop")
        catogoreyList.add("Hotel")
        catogoreyList.add("Other")

        spinCategory = findViewById(R.id.spinner)
        ownerPhoenNumberEditText = findViewById(R.id.ownerPhoneEditText)
        bussinesNameEditText = findViewById(R.id.bussinessnameEditText)
        placeNameEdittext = findViewById(R.id.placenameEditText)
        ownerNameEditText = findViewById(R.id.ownerNameEditText)
        uploadImageView = findViewById(R.id.uploadImageView)


        uploadImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_photo))

        uploadButton = findViewById(R.id.uploadButton)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            catogoreyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinCategory.adapter = adapter

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        val db = FirebaseFirestore.getInstance()

        Log.e("user", FirebaseAuth.getInstance().currentUser.toString())


        uploadButton.setOnClickListener {


            if (ownerNameEditText.text.toString().isEmpty() && bussinesNameEditText.text.isEmpty() && ownerPhoenNumberEditText.text.isEmpty()) {

                Toast.makeText(this, "please enter all Fields", Toast.LENGTH_SHORT).show()
            } else {


                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()


                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()


                val child = imagesRef?.child(imageName)
                val uploadTask = child?.putBytes(data)


                uploadTask?.addOnFailureListener {
                    Toast.makeText(this, "upload Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }?.addOnSuccessListener {

                    child.downloadUrl.addOnSuccessListener {
                        Unit
                        Toast.makeText(applicationContext, "Url : " + it.path, Toast.LENGTH_SHORT).show()
                        Log.e("Image Url", "Url : " + it.path)
                        Log.e("Image Url", "Url : " + it.toString())


                        locationManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        var latitude = location.latitude
                        var longitude = location.longitude

                        var shop = hashMapOf(
                            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
                            "shopCatogorey" to spinCategory.selectedItem.toString(),
                            "businessName" to bussinesNameEditText.text.toString(),
                            "ownerName" to ownerNameEditText.text.toString(),
                            "ownerPhoneNum" to ownerPhoenNumberEditText.text.toString(),
                            "businessPlace" to placeNameEdittext.text.toString(),
                            "url" to it.toString(),
                            "latitude" to latitude,
                            "longitude" to longitude

                        )


                        db.collection("shops")
                            .add(shop)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Document", "DocumentSnapshot added with ID: ${documentReference.id}")
                                Toast.makeText(this, "Successfully added Document Good Job ", Toast.LENGTH_SHORT).show()
                                bussinesNameEditText.setText("")
                                ownerNameEditText.setText("")
                                ownerPhoenNumberEditText.setText("")
                                placeNameEdittext.setText("")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Doc Error Occures", "Error adding document", e)
                                Toast.makeText(this, "Sorry something went Wrong+$e", Toast.LENGTH_SHORT).show()
                            }


                    }.addOnFailureListener {
                        Unit
                        Toast.makeText(applicationContext, "Failed : " + it.message, Toast.LENGTH_SHORT).show()
                        Log.e("Error Image Url", "Failed : " + it.message)


                    }
                    Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                    uploadImageView.invalidate()
                    uploadImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_photo))


                }?.addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                }

            }
        }

        uploadImageView.setOnClickListener {

            Toast.makeText(this, "you Clicked the Image View", Toast.LENGTH_LONG).show()
            getPhoto()
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getPhoto()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data?.data

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                //val baos = ByteArrayOutputStream()
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                uploadImageView.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        val a = Intent(Intent.ACTION_MAIN)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menulog, menu)
        return true



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {

            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, MainActivity::class.java))

            return true

        } else {
            return super.onOptionsItemSelected(item)
        }

    }

    companion object {
        fun isOnline(uploadActivity: UploadActivity): Boolean {
            val connectivityManager = uploadActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

}


