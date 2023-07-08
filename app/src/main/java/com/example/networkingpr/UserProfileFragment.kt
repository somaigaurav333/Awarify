package com.example.networkingpr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


class UserProfileFragment : Fragment() {

    lateinit var fireBaseAuth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireBaseAuth = FirebaseAuth.getInstance()
        val emailtextview: TextView = view.findViewById(R.id.profileEmail)
        val userAvatar: ImageView = view.findViewById(R.id.userAvatar)
        emailtextview.text = fireBaseAuth.currentUser?.email.toString()
//        userAvatar.setImageURI(fireBaseAuth.currentUser?.photoUrl)
        userAvatar.setImageResource(R.drawable.ic_baseline_person_24)
        view.findViewById<TextView>(R.id.profile_username).text = fireBaseAuth.currentUser?.displayName.toString()


    }

}