package com.example.networkingpr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Settings"

        val userPreferences =
            getSharedPreferences(getString(R.string.userpref), Context.MODE_PRIVATE)
        val userPreferencesEditor: SharedPreferences.Editor = userPreferences.edit()

        val darkthemetoggle: SwitchCompat = findViewById(R.id.darkthemetoggle)
        darkthemetoggle.isChecked = global.darktheme

        darkthemetoggle.setOnCheckedChangeListener { _, _ ->
            global.darktheme = !global.darktheme
            if (global.darktheme) {
                userPreferencesEditor.putBoolean("darktheme", true)
                userPreferencesEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                userPreferencesEditor.putBoolean("darktheme", false)
                userPreferencesEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val renderimagestoggle: SwitchCompat = findViewById(R.id.renderimagestoggle)
        renderimagestoggle.isChecked = global.renderimages

        renderimagestoggle.setOnCheckedChangeListener { _, _ ->
            global.renderimages = !global.renderimages
            renderimagestoggle.isChecked = global.renderimages
            userPreferencesEditor.putBoolean("renderimages", global.renderimages)
            userPreferencesEditor.apply()
        }



        val signoutbtn : Button = findViewById(R.id.signout)

        signoutbtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            val finishMainActivity = Intent("finish_main_activity")
            sendBroadcast(finishMainActivity)
            finish()
        }

        val deleteaccbtn : Button = findViewById(R.id.deleteacc)

        deleteaccbtn.setOnClickListener{
            FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener {
                Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)

                val finishMainActivity = Intent("finish_main_activity")
                sendBroadcast(finishMainActivity)
                finish()
            }

           
        }





        }
}