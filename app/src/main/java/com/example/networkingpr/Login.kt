package com.example.networkingpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser
        if (user != null) {
            Toast.makeText(this, "Signing In ${firebaseAuth.currentUser?.email.toString()}" , Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)

        } else {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
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