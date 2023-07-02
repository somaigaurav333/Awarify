package com.example.networkingpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.*


import com.example.networkingpr.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupbtn.setOnClickListener {
            val email = binding.signupmail.text.toString()
            val password = binding.signuppassword.text.toString()
            val confirmpass = binding.confirmsignuppass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password != confirmpass) {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                } else {
                    binding.signupbtn.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE


                    GlobalScope.launch(Dispatchers.IO) {

                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                            if(it.isSuccessful){
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(Dispatchers.Main){
                                        Toast.makeText(this@SignUp, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@SignUp, Login::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }else{
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(Dispatchers.Main){
                                        Toast.makeText(this@SignUp, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                withContext(Dispatchers.Main){
                                    binding.signupmail.text.clear()
                                    binding.signuppassword.text.clear()
                                    binding.confirmsignuppass.text.clear()
                                    binding.progressBar.visibility= View.GONE
                                    binding.signupbtn.isEnabled = true
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show()
            }
        }


    }



}