package com.example.networkingpr

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


//Recycler View Save State, or shift to view model


object global {

    var darktheme = true
    var renderimages = true
    var position = 0
    var _tempTitle: String = "Awarify"

}


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var navView: NavigationView
    lateinit var homeFragment: HomeFragment
    lateinit var userProfileFragment: UserProfileFragment
    lateinit var exploreFragment: ExploreFragment

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //loading firebase auth and user
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser



        if (user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

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


        // Loading fragment instances

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


        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.nav_settings) {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                drawerLayout.close()
            }
            true
        }


        //Code for bottom app bar

        val navbar: BottomNavigationView = findViewById(R.id.navbar)
//        navbar.menu.findItem(R.id.home).isChecked=true

        navbar.setOnItemSelectedListener {
            if (it.itemId == R.id.home) {
                replaceFragment(homeFragment)
                supportActionBar!!.hide()

            } else if (it.itemId == R.id.exp) {

                replaceFragment(exploreFragment)
                supportActionBar!!.hide()
            } else if (it.itemId == R.id.userprofile) {

                replaceFragment(userProfileFragment)
                supportActionBar!!.show()
                supportActionBar?.title = "Profile"


            }

            true
        }


        replaceFragment(homeFragment)


        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, intent: Intent) {
                val action = intent.action
                if (action == "finish_main_activity") {
                    finish()
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("finish_main_activity"))


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




