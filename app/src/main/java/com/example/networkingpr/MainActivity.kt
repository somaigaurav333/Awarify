package com.example.networkingpr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//Recycler View Save State, or shift to view model


object global {

    var darktheme = true
    var renderimages = true
    var position = 0
    var _tempTitle: String = "Awarify"

}


class MainActivity : AppCompatActivity() {
    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var darktoggleswitch: SwitchCompat
    lateinit var renderImagesToggleButton: SwitchCompat
    private lateinit var navView: NavigationView
    lateinit var homeFragment: HomeFragment
    lateinit var userProfileFragment: UserProfileFragment
    lateinit var exploreFragment: ExploreFragment

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }


        //Setting User Preferences and loading dark mode and render images settings
        val userPreferences =
            getSharedPreferences(getString(R.string.userpref), Context.MODE_PRIVATE)
        val userPreferencesEditor: SharedPreferences.Editor = userPreferences.edit()


        if (userPreferences.contains("darktheme")) {
            if (userPreferences.getBoolean("darktheme", true)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                global.darktheme = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                global.darktheme = false
            }
        } else {
            global.darktheme =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
            userPreferencesEditor.putBoolean("darktheme", global.darktheme)
            userPreferencesEditor.apply()
        }

        if (userPreferences.contains("renderimages")) {
            global.renderimages = userPreferences.getBoolean("renderimages", true)
        }


        //loading firebase auth and user
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser



        if (user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }


        homeFragment = HomeFragment()
        userProfileFragment = UserProfileFragment()
        exploreFragment = ExploreFragment()


        // Setting up the Navigation View and Drawer Layout

        drawerLayout = findViewById(R.id.drawerLayout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navView = findViewById(R.id.navView)


//        darktoggleswitch.isChecked = global.darktheme


        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.darkthemetoggle) {
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

            } else if (it.itemId == R.id.renderimagetoggle) {
                global.renderimages = !global.renderimages
                renderImagesToggleButton.isChecked = global.renderimages
                userPreferencesEditor.putBoolean("renderimages", global.renderimages)
                userPreferencesEditor.apply()
            }
            true
        }


        //Code for bottom app bar

        val navbar: BottomNavigationView = findViewById(R.id.navbar)

        navbar.setOnItemSelectedListener {
            if (it.itemId == R.id.home) {

                replaceFragment(homeFragment)

                supportActionBar!!.hide()


            } else if (it.itemId == R.id.exp) {

                replaceFragment(exploreFragment)
                supportActionBar!!.hide()
            }
            else if (it.itemId == R.id.userprofile) {

                replaceFragment(userProfileFragment)
                supportActionBar!!.show()




            }

            true
        }


        replaceFragment(homeFragment)


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(frag: Fragment): Boolean {
        supportFragmentManager.beginTransaction().apply {
            disallowAddToBackStack()
            replace(R.id.fragment, frag)
            commit()
        }
        return true
    }


}




