package com.renzam.shelf.data

import android.app.Application
import android.app.ProgressDialog
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ViewModel(application: Application): AndroidViewModel(application) {



    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.getReference()




    var repo: Repository? = null

    init {
        repo = Repository(application)
    }

    fun uploadFile(filePath: Uri) {


        val progressDialog = ProgressDialog(getApplication())
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val ref = storageReference.child("images/" + UUID.randomUUID().toString())
        ref.putFile(filePath)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener{ lt -> Unit
                    repo?.uploadedFile(ref.path,lt.path)
                }

                progressDialog.dismiss()
                Toast.makeText(getApplication(), "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(getApplication() ,"Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }



    }

    fun geturl(){



    }

}