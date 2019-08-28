package com.renzam.shelf.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.renzam.shelf.R

import com.renzam.shelf.data.ViewModel
import com.renzam.shelf.databinding.ActivityUploadBinding
import kotlin.collections.ArrayList

class UploadActivity : AppCompatActivity() {

    lateinit var uploadImageView: ImageView
    lateinit var spinCategory: Spinner
    lateinit var catogoreyList: ArrayList<String>
    lateinit var locationManager: LocationManager
    lateinit var location: Location
    lateinit var bitmap: Bitmap
    lateinit var viemodelDummy: ViewModel


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

        viewModelCls.Goodnews(this@UploadActivity)

        viemodelDummy = viewModelCls

        if (!Companion.isOnline(this)) {
            Toast.makeText(this, "Turn On Internet Connection", Toast.LENGTH_LONG).show()
        }

        catogoreyList = ArrayList()
        catogoreyList.add("select Catogorey")
        catogoreyList.add("department store")
        catogoreyList.add("supermarket")
        catogoreyList.add("grocer")
        spinCategory = findViewById(R.id.spinner)
        uploadImageView = findViewById(R.id.uploadImageView)
        catogoreyList.add("greengrocer")
        catogoreyList.add("bookShop")
        catogoreyList.add("stationary")
        catogoreyList.add("clothes shop")
        catogoreyList.add("optican")
        catogoreyList.add("petshop")
        catogoreyList.add("Hotel")
        catogoreyList.add("Other")


        uploadImageView.setOnClickListener {

            getPhoto()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            catogoreyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinCategory.adapter = adapter

        spinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                viemodelDummy.getCatagorey(selectedItem)


                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }

                locationManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                var latitude = location.latitude
                var longitude = location.longitude
                viemodelDummy.getLocation(latitude, longitude)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
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

    companion object {
        fun isOnline(uploadActivity: UploadActivity): Boolean {
            val connectivityManager =
                uploadActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


    }
}


