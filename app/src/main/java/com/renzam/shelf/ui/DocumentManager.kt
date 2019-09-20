package com.renzam.shelf.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.renzam.shelf.R
import com.renzam.shelf.data.DmviewModel
import com.renzam.shelf.data.ViewModel
import com.renzam.shelf.databinding.ActivityUploadBinding
import com.squareup.picasso.Picasso

class DocumentManager : AppCompatActivity() {

    lateinit var businessNameEt: EditText
    lateinit var placeNameEt: EditText
    lateinit var ownerNameEt: EditText
    lateinit var phoneNumber: EditText
    lateinit var imageView: ImageView

    lateinit var catogoreyList: ArrayList<String>

    lateinit var deleteBtn: Button
    lateinit var edtBtn: Button
    var lat: Double = 12.1
    var long: Double = 12.1
    lateinit var shopIn: String
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_manager)


        businessNameEt = findViewById(R.id.businessNameEt)
        placeNameEt = findViewById(R.id.placeNameEt)
        ownerNameEt = findViewById(R.id.ownerNameEt)
        phoneNumber = findViewById(R.id.phoneNumberEt)
        imageView = findViewById(R.id.imageViewIm)

        spinner = findViewById(R.id.catpgoryEt)

        catogoreyList = ArrayList()

        catogoreyList.add("Select Category")
        catogoreyList.add("Department store")
        catogoreyList.add("Supermarket")
        catogoreyList.add("Grocer")
        catogoreyList.add("Greengrocer")
        catogoreyList.add("BookShop")
        catogoreyList.add("Stationary")
        catogoreyList.add("Clothes shop")
        catogoreyList.add("Optican")
        catogoreyList.add("Petshop")
        catogoreyList.add("Hotel")
        catogoreyList.add("Other")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            catogoreyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinner.adapter = adapter


        deleteBtn = findViewById(R.id.deleteBtn)
        edtBtn = findViewById(R.id.editFieldBtn)


        val progressDialogStart = ProgressDialog(this)
        progressDialogStart.setTitle("Fetchng Datas...")
        progressDialogStart.setMessage("Please Wait.. :) ")
        progressDialogStart.show()

        val db = FirebaseFirestore.getInstance()

        var intent = intent
        var docId: String = intent.getStringExtra("docid")

        val docRef = db.collection("shops").document(docId)

        docRef.get().addOnCompleteListener {

            if (it.isSuccessful) {

                var result = it.result

                Log.i("datas************", result!!["url"].toString())

                spinner.setSelection(catogoreyList.indexOf(result["shopCatogorey"].toString()))
                businessNameEt.setText(result["businessName"].toString())
                ownerNameEt.setText(result["ownerName"].toString())
                placeNameEt.setText(result["businessPlace"].toString())
                phoneNumber.setText(result["ownerPhoneNum"].toString())


                lat = result["latitude"] as Double
                long = result["longitude"] as Double

                shopIn = result["businessName"].toString()

                Picasso.get()
                    .load(result["url"].toString())
                    .placeholder(R.drawable.noimage)
                    .into(imageView)
                progressDialogStart.dismiss()
            }
        }

        deleteBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Do You Want To Delete This")
                .setMessage("It Can Delete Your Uploaded Data Compleately... ")
                .setPositiveButton("Yes") { dialog, which ->

                    val progressDialogDelete = ProgressDialog(this)
                    progressDialogDelete.setTitle("Uploading...")
                    progressDialogDelete.setMessage("Please Wait.. :) ")
                    progressDialogDelete.show()

                    db.collection("shops").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MyUploadActivity::class.java))
                            progressDialogDelete.dismiss()
                        }.addOnFailureListener {

                            Toast.makeText(this, "Something Went Wrong Please Try Again Later", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("Error Deletionn ******", it.message)
                            progressDialogDelete.dismiss()
                        }


                }.setNeutralButton("No") { dialog, which ->

                    dialog.dismiss()

                }
                .create()
                .show()

        }
        edtBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Update Document")
                .setMessage("Do You Want To Update It")
                .setPositiveButton("Yes") { dialog, which ->

                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Uploading...")
                    progressDialog.setMessage("Please Wait.. :) ")
                    progressDialog.show()

                    var docRefUdp: DocumentReference = db.collection("shops").document(docId)

                    docRefUdp.update("shopCatogorey", spinner.selectedItem.toString())
                    docRefUdp.update("businessName", businessNameEt.text.toString())
                    docRefUdp.update("ownerName", ownerNameEt.text.toString())
                    docRefUdp.update("businessPlace", placeNameEt.text.toString())
                    docRefUdp.update("ownerPhoneNum", phoneNumber.text.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                            startActivity(Intent(this, MyUploadActivity::class.java))
                        }.addOnFailureListener {
                            Toast.makeText(this, "Sorry Cannot Update Please Try Agin Later", Toast.LENGTH_SHORT).show()
                        }

                }
                .setNeutralButton("No") { dialog, which ->

                    dialog.dismiss()
                }
                .create()
                .show()

        }

        imageView.setOnClickListener {

            val intent = Intent(this, ImageUpdateActivity::class.java)
            intent.putExtra("docIdImg", docId)
            startActivity(intent)

        }


    }
}