package com.example.networkingpr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

import com.example.networkingpr.databinding.ActivityLoginBinding


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = getSharedPreferences(getString(R.string.userpref) , Context.MODE_PRIVATE )
        val userPreferencesEditor : SharedPreferences.Editor = userPreferences.edit()

        if(userPreferences.contains("darktheme")){
            if(userPreferences.getBoolean("darktheme", true)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                global.darktheme = true
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                global.darktheme = false
            }
        }else{
            global.darktheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
            userPreferencesEditor.putBoolean("darktheme", global.darktheme)
            userPreferencesEditor.apply()
        }


        firebaseAuth = FirebaseAuth.getInstance()



        val user = firebaseAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Signing In ${firebaseAuth.currentUser?.email.toString()}" , Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)

        } else {
            Toast.makeText(this, "Please Log In", Toast.LENGTH_SHORT).show()
        }

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.login.setOnClickListener{

            val email = binding.signinemail.text.toString()
            val pass = binding.signinpassword.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty()){
                binding.progressBarsignin.visibility = View.VISIBLE
                binding.login.isEnabled = false


                GlobalScope.launch(Dispatchers.IO) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.Main){
                                if (it.isSuccessful) {
                                    binding.signinemail.text.clear()
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@Login, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                                binding.signinpassword.text.clear()
                                binding.login.isEnabled = true
                                binding.progressBarsignin.visibility = View.GONE
                            }
                        }
                    }
                }

            }else{
                Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show()
            }
        }


    }
}