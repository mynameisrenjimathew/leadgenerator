package com.renzam.shelf.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.renzam.shelf.R

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1234
    lateinit var logInButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


          if (FirebaseAuth.getInstance().currentUser != null){

              goNextfun()
          }else {

              val providers = arrayListOf(
                  AuthUI.IdpConfig.EmailBuilder().build(),
                  AuthUI.IdpConfig.PhoneBuilder().build(),
                  AuthUI.IdpConfig.GoogleBuilder().build()
              )

              startActivityForResult(
                  AuthUI.getInstance()
                      .createSignInIntentBuilder()
                      .setAvailableProviders(providers)
                      .build(),
                  RC_SIGN_IN
              )
          }

        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                val user = FirebaseAuth.getInstance().currentUser
                goNextfun()
                // ...
            } else {

                Toast.makeText(this,"Sorry Something Went Wrong :(",Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun goNextfun() {
        startActivity(Intent(this, UploadActivity::class.java))
    }


}

