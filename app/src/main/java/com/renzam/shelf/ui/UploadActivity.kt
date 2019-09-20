package com.renzam.shelf.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.renzam.shelf.R

import com.renzam.shelf.data.ViewModel
import com.renzam.shelf.databinding.ActivityUploadBinding
import kotlinx.android.synthetic.main.activity_document_manager.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    lateinit var uploadImageView: ImageView
    lateinit var spinCategory: Spinner
    lateinit var catogoreyList: ArrayList<String>
    lateinit var locationManager: LocationManager
    lateinit var bitmap: Bitmap
    lateinit var viemodelDummy: ViewModel
    lateinit var locationListner: LocationListener

    lateinit var progressBar: ProgressBar


    //    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val viewModelCls = ViewModelProviders.of(this)
            .get(ViewModel::class.java)

        DataBindingUtil.setContentView<ActivityUploadBinding>(
            this, R.layout.activity_upload
        ).apply {
            this.lifecycleOwner = this@UploadActivity
            this.viewmodel = viewModelCls
        }



        viemodelDummy = viewModelCls

        catogoreyList = ArrayList()
        catogoreyList.add("Select Category")
        catogoreyList.add("Department store")
        catogoreyList.add("Supermarket")
        catogoreyList.add("Grocer")
        spinCategory = findViewById(R.id.spinner)
        uploadImageView = findViewById(R.id.uploadImageView)
        catogoreyList.add("Greengrocer")
        catogoreyList.add("BookShop")
        catogoreyList.add("Stationary")
        catogoreyList.add("Clothes shop")
        catogoreyList.add("Optican")
        catogoreyList.add("Petshop")
        catogoreyList.add("Hotel")
        catogoreyList.add("Other")

        progressBar = findViewById(R.id.progressBar)


        locationManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager)

//        myBitmap = BitmapFactory.decodeResource(resources, R.drawable.noimage)
//        uploadImageView.setImageBitmap(myBitmap)


        uploadImageView.invalidate()
        var bitmapDrawable: BitmapDrawable = uploadImageView.drawable as BitmapDrawable
        var bimapImagV: Bitmap = bitmapDrawable.bitmap
        viemodelDummy.getBitmap(bimapImagV)



        uploadImageView.setOnClickListener {

            getPhoto()
        }
        viewModelCls.success.observe(this, Observer {

            if (it == "on") {

                bussinessnameEditText.visibility = View.INVISIBLE
                placenameEditText.visibility = View.INVISIBLE
                ownerNameEditText.visibility = View.INVISIBLE
                ownerPhoneEditText.visibility = View.INVISIBLE
                spinCategory.visibility = View.INVISIBLE
                uploadImageView.visibility = View.INVISIBLE
                uploadButton.isEnabled = false



                progressBar.visibility = View.VISIBLE
            }

            if (it == "success") {

                uploadImageView.visibility = View.VISIBLE

                uploadImageView.invalidate()
                uploadImageView.setImageResource(R.drawable.noimage)
                spinCategory.setSelection(0)

                spinCategory.visibility = View.VISIBLE
                bussinessnameEditText.visibility = View.VISIBLE
                placenameEditText.visibility = View.VISIBLE
                ownerNameEditText.visibility = View.VISIBLE
                ownerPhoneEditText.visibility = View.VISIBLE

                bussinessnameEditText.requestFocus()

                progressBar.visibility = View.GONE

                bussinessnameEditText.setText("")
                placenameEditText.setText("")
                ownerNameEditText.setText("")
                ownerPhoneEditText.setText("")

                uploadButton.isEnabled = true


            }

        })
        viewModelCls.fbusinessname.observe(this, Observer {

            if (it == "Failed") {

                bussinessnameEditText.requestFocus()
            }

        })
        viewModelCls.fplaceName.observe(this, Observer {

            if (it == "Failed") {

                placenameEditText.requestFocus()
            }

        })
        viewModelCls.fownerName.observe(this, Observer {

            if (it == "Failed") {

                ownerNameEditText.requestFocus()
            }

        })
        viewModelCls.fphoneNumber.observe(this, Observer {

            if (it == "Failed") {

                ownerPhoneEditText.requestFocus()
            }

        })


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            catogoreyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)



        spinCategory.adapter = adapter

//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListner)
//
//        }else{
////            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        }

        spinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                viemodelDummy.getCatagorey(selectedItem)
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListner)
                } else {
                    ActivityCompat.requestPermissions(
                        this@UploadActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        locationListner = object : LocationListener {
            override fun onLocationChanged(location: Location) {


                viemodelDummy.getLocation(location.latitude, location.longitude)

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}

        }

        if (!isLocationEnabled(this)) {

            Toast.makeText(this, "Please Check Gps Connection", Toast.LENGTH_SHORT).show()

        }

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

        } else if (item.itemId == R.id.delete_Account) {

            AlertDialog.Builder(this)
                .setTitle("Are You Sure !!")
                .setMessage("This is Delete Your Account Compleately, You Cant Acess Your data")
                .setPositiveButton("Yes") { dialog, which ->

                    AuthUI.getInstance()
                        .delete(this).addOnCompleteListener {

                            Toast.makeText(this, "Your Account is Deleted", Toast.LENGTH_SHORT).show()
                            Log.i("Auth Deleted", it.toString())
                        }
                }
                .setNeutralButton("No") { dialog, which ->

                    dialog.dismiss()

                }
                .create()
                .show()

            return true
        } else if (item.itemId == R.id.myUploads) {

            startActivity(Intent(this, MyUploadActivity::class.java))
            return true

        } else {
            return super.onOptionsItemSelected(item)
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
                viemodelDummy.getBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    fun isLocationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return lm.isLocationEnabled
        } else {
            // This is Deprecated in API 28
            val mode = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            return mode != Settings.Secure.LOCATION_MODE_OFF
        }

    }



}




