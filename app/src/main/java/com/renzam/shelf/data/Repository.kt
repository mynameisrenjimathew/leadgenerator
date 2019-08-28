//package com.renzam.shelf.data
//
//import android.app.Application
//import android.app.ProgressDialog
//import android.content.Context
//import android.graphics.Bitmap
//import android.util.Log
//import android.widget.Toast
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import java.io.ByteArrayOutputStream
//import java.util.*
//
//class Repository {
//
//    lateinit var context: Context
//
//
//    fun UploadToDatabase(
//        catogoryOfShop: String,
//        bussinessName: String,
//        placeName: String,
//        ownerName: String,
//        ownerPhoneNumber: String,
//        bitmap: Bitmap,
//        latitude: Double,
//        longitude: Double
//    ) {
//
//
//        val db = FirebaseFirestore.getInstance()
//        var shop = hashMapOf(
//                            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
//                            "shopCatogorey" to catogoryOfShop,
//                            "businessName" to bussinessName,
//                            "ownerName" to ownerName,
//                            "ownerPhoneNum" to ownerPhoneNumber,
//                            "businessPlace" to placeName,
//                            "url" to "",
//                            "latitude" to latitude,
//                            "longitude" to longitude
//
//                        )
//
//
//                        db.collection("shops")
//                            .add(shop)
//                            .addOnSuccessListener { documentReference ->
//                                //Log.d("Document", "DocumentSnapshot added with ID: ${documentReference.id}")
//                                Toast.makeText(context, "Successfully added Document Good Job ", Toast.LENGTH_SHORT).show()
//
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w("Doc Error Occures", "Error adding document", e)
//                                Toast.makeText(context, "Sorry something went Wrong+$e", Toast.LENGTH_SHORT).show()
//                            }
//
////        var storage = FirebaseStorage.getInstance()
////
////        var storageRef = storage.reference
////
////        val imageName = UUID.randomUUID().toString() + ".jpg"
////        var imagesRef: StorageReference? = storageRef.child("images")
////
////
////        val baos = ByteArrayOutputStream()
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////        val data = baos.toByteArray()
////
////        val child = imagesRef?.child(imageName)
////        val uploadTask = child?.putBytes(data)
////
////        uploadTask?.addOnFailureListener {
////
////            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
////            Log.i("Image Uload **************","Image Upload Failed+++")
////
////        }?.addOnSuccessListener{
////
////            child.downloadUrl.addOnSuccessListener {
////                Log.i("Download Url Repo**********",it.toString())
////            }.addOnFailureListener {
////                Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
////                Log.i("Download Url Repo**********","sorry upload failed")
////            }
////
////        }
//
//    }
//
//
////        var storage = FirebaseStorage.getInstance()
////
////        var storageRef = storage.reference
////
////        val imageName = UUID.randomUUID().toString() + ".jpg"
////        var imagesRef: StorageReference? = storageRef.child("images")
////
////
////        val db = FirebaseFirestore.getInstance()
////
////        Log.e("user", FirebaseAuth.getInstance().currentUser.toString())
////
////        val baos = ByteArrayOutputStream()
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////        val data = baos.toByteArray()
////
////        val child = imagesRef?.child(imageName)
////        val uploadTask = child?.putBytes(data)
////
////        val progressDialog = ProgressDialog(context)
////                progressDialog.setTitle("Uploading...")
////                progressDialog.show()
////
////        uploadTask?.addOnFailureListener {
////                                Toast.makeText(context, "upload Failed", Toast.LENGTH_SHORT).show()
////                    progressDialog.dismiss()
////                }?.addOnSuccessListener {
////
////                    child.downloadUrl.addOnSuccessListener {
////                        Unit
////                        Toast.makeText(context, "Url : " + it.path, Toast.LENGTH_SHORT).show()
////                        Log.e("Image Url", "Url : " + it.path)
////                        Log.e("Image Url", "Url : " + it.toString())
////
////
////
////
////                        var shop = hashMapOf(
////                            "userId" to FirebaseAuth.getInstance().currentUser?.uid,
////                            "shopCatogorey" to catogoryOfShop,
////                            "businessName" to bussinessName,
////                            "ownerName" to ownerName,
////                            "ownerPhoneNum" to ownerPhoneNumber,
////                            "businessPlace" to placeName,
////                            "url" to it.toString(),
////                            "latitude" to latitude,
////                            "longitude" to longitude
////
////                        )
////
////
////                        db.collection("shops")
////                            .add(shop)
////                            .addOnSuccessListener { documentReference ->
////                                //Log.d("Document", "DocumentSnapshot added with ID: ${documentReference.id}")
////                                Toast.makeText(context, "Successfully added Document Good Job ", Toast.LENGTH_SHORT).show()
////
////                            }
////                            .addOnFailureListener { e ->
////                                Log.w("Doc Error Occures", "Error adding document", e)
////                                Toast.makeText(context, "Sorry something went Wrong+$e", Toast.LENGTH_SHORT).show()
////                            }
////
////
////                    }.addOnFailureListener {
////                        Unit
////                        Toast.makeText(context, "Failed : " + it.message, Toast.LENGTH_SHORT).show()
////                        Log.e("Error Image Url", "Failed : " + it.message)
////                    }
////                    Toast.makeText(context, "uploaded", Toast.LENGTH_LONG).show()
////
////
////
////                }?.addOnProgressListener { taskSnapshot ->
////                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
////                        .totalByteCount
////                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
////                }
////
////            }
//
//
//        fun getThisContext(contextThis: Context) {
//
//            context = contextThis
//
//        }
//
//
//    }