package com.renzam.shelf.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.renzam.shelf.R
import io.grpc.Context
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    lateinit var bussinesNameEditText: EditText
    lateinit var ownerNameEditText: EditText
    lateinit var uploadButton: Button
    lateinit var uploadImageView: ImageView
    lateinit var spinCategory: Spinner
    lateinit var ownerPhoenNumberEditText: EditText
    lateinit var catogoreyList: ArrayList<String>

    lateinit var bitmap: Bitmap

    var imageUrl = ""

    var storage = FirebaseStorage.getInstance()

    var storageRef = storage.reference

    val imageName = UUID.randomUUID().toString() + ".jpg"
    var imagesRef: StorageReference? = storageRef.child("images")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)


        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)

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
        ownerNameEditText = findViewById(R.id.ownerNameEditText)
        uploadImageView = findViewById(R.id.uploadImageView)

        uploadButton = findViewById(R.id.uploadButton)

        val adapter = ArrayAdapter(
            this, // Context
            android.R.layout.simple_spinner_item, // Layout
            catogoreyList // Array
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinCategory.adapter = adapter



        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        }


        val db = FirebaseFirestore.getInstance()

        Log.e("user", FirebaseAuth.getInstance().currentUser.toString())


        uploadButton.setOnClickListener {

            var shop = hashMapOf(
                "userId" to FirebaseAuth.getInstance().currentUser?.uid,
                "shopCatogorey" to spinCategory.selectedItem.toString(),
                "businessName" to bussinesNameEditText.text.toString(),
                "ownerName" to ownerNameEditText.text.toString(),
                "ownerPhoneNum" to ownerPhoenNumberEditText.text.toString(),
                "url" to "https://firebasestorage.googleapis.com/v0/b/shelf-a7d22.appspot.com/o/images%2F50c41036-8eaf-458a-910b-223732e19cfa.jpg?alt=media&token=d2d801c2-24a7-463b-8288-d5ea2b7779a0"
            )


            db.collection("shops")
                .add(shop)
                .addOnSuccessListener { documentReference ->
                    Log.d("Document", "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Successfully added Document Good Job ", Toast.LENGTH_SHORT).show()
                    bussinesNameEditText.setText("")
                    ownerNameEditText.setText("")
                    ownerPhoenNumberEditText.setText("")
                }
                .addOnFailureListener { e ->
                    Log.w("Doc Error Occures", "Error adding document", e)
                    Toast.makeText(this, "Sorry something went Wrong+$e", Toast.LENGTH_SHORT).show()
                }

            photoUpload()

        }
//        logOutButton.setOnClickListener {
//

//        }


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
                val baos = ByteArrayOutputStream()
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                uploadImageView.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun photoUpload() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef?.child(imageName)?.putBytes(data)

        uploadTask?.addOnFailureListener {
            Toast.makeText(this, "upload Failed", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }?.addOnSuccessListener {

            Toast.makeText(this, "uploaded", Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
            uploadImageView.invalidate()
            uploadImageView.setImageBitmap(null)

        }?.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                .totalByteCount
            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
        }


        //url
//        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            return@Continuation imagesRef?.downloadUrl
//        })?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUri = task.result
//                Log.i("download image uri",downloadUri.toString())
//            } else {
//                // Handle failures
//                Log.i("Error","Url Error")
//
//            }
//        }

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


}

