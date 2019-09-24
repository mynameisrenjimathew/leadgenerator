package com.renzam.shelf.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.renzam.shelf.R
import android.content.BroadcastReceiver
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN = 123
    lateinit var internetErrorBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        internetErrorBtn = findViewById(R.id.internerBtn)

        if (!isInternet(this)) {

            AlertDialog.Builder(this)
                .setTitle("Network Error")
                .setMessage("Please Check Your Internet Connection")
                .setPositiveButton("Ok", null)
                .show()
            internetErrorBtn.visibility = View.VISIBLE

        } else {


            if (FirebaseAuth.getInstance().currentUser != null) {

                goNextfun()
            } else {

                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.loginicon)
                        .build(),
                    RC_SIGN_IN
                )
            }
        }
        internetErrorBtn.setOnClickListener {

            if (FirebaseAuth.getInstance().currentUser != null) {

                goNextfun()
            } else {

                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.loginicon)
                        .build(),
                    RC_SIGN_IN
                )
            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                val user = FirebaseAuth.getInstance().currentUser
                goNextfun()

            } else {

                Toast.makeText(this, "Sorry Something Went Wrong :(", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun goNextfun() {
        startActivity(Intent(this, MyUploadActivity::class.java))
        this.finish()
    }

    fun isInternet(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}