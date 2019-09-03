package com.renzam.shelf.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.common.base.MoreObjects
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
    lateinit var bitmap: Bitmap
    lateinit var viemodelDummy: ViewModel
    lateinit var myBitmap: Bitmap
    lateinit var locationListner: LocationListener


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


        locationManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager)

        myBitmap = BitmapFactory.decodeResource(resources, R.drawable.noimage)
        uploadImageView.setImageBitmap(myBitmap)

        uploadImageView.invalidate()
        var bitmapDrawable: BitmapDrawable = uploadImageView.drawable as BitmapDrawable
        var bimapImagV: Bitmap = bitmapDrawable.bitmap
        viemodelDummy.getBitmap(bimapImagV)


        uploadImageView.setOnClickListener {

            uploadImageView.invalidate()
            uploadImageView.setImageBitmap(null)

            getPhoto()

        }
        viewModelCls.success.observe(this, Observer {

            if (it == "success") {

                uploadImageView.invalidate()
                uploadImageView.setImageBitmap(null)
                uploadImageView.setImageBitmap(myBitmap)

            }
        })


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            catogoreyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinCategory.adapter = adapter

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListner)

        }else{
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }



        spinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                viemodelDummy.getCatagorey(selectedItem)
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListner)
                }else{
                    ActivityCompat.requestPermissions(this@UploadActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }



               // viemodelDummy.getLocation(latitudeOn, longitudeOn)


            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        locationListner = object : LocationListener {
            override fun onLocationChanged(location: Location) {

                viemodelDummy.getLocation(location.latitude,location.longitude)

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
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

//            AuthUI.getInstance()
//                .delete(this).addOnCompleteListener {
//
//                    Toast.makeText(this,"Your Account is Successfully Deleted",Toast.LENGTH_SHORT).show()
//                    Log.i("Auth Deleted",it.toString())
//                }

            Toast.makeText(this, "Sorry this Function Is Disabled Please Try Again Later :(", Toast.LENGTH_SHORT).show()
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


}




