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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

}



class MainActivity : AppCompatActivity() {
    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var darktoggleswitch : SwitchCompat
    lateinit var renderImagesToggleButton : SwitchCompat
    private lateinit var navView: NavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setting User Preferences and loading dark mode and render images settings
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

        if(userPreferences.contains("renderimages")){
            global.renderimages = userPreferences.getBoolean("renderimages", true)
        }

        // Setting up the Navigation View and Drawer Layout

        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView = findViewById(R.id.navView)


        darktoggleswitch = drawerLayout.findViewById(R.id.darkthemetoggle)
        darktoggleswitch.isChecked = global.darktheme
        renderImagesToggleButton = drawerLayout.findViewById(R.id.renderimagetoggle)
        renderImagesToggleButton.isChecked = global.renderimages
        val searchbtn : Button = drawerLayout.findViewById(R.id.Search)



        //Click Listener for Dark Theme Toggle Button

        darktoggleswitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
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
        })

        //Click Listener for Render Images Toggle Button

        renderImagesToggleButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                global.renderimages = !global.renderimages
                renderImagesToggleButton.isChecked = global.renderimages
                userPreferencesEditor.putBoolean("renderimages", global.renderimages)
                userPreferencesEditor.apply()
            }
        })



//        navView.setNavigationItemSelectedListener {
//            when (it.itemId) {
////                R.id.darkthemetoggle -> {
////                    global.darktheme = findViewById<ToggleButton>(R.id.darkthemetoggle).isActivated
////
////                    if (!global.darktheme) {
////                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
////                    } else {
////                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
////                    }
////                }
////                R.id.renderimagetoggle -> {
////                    global.renderimages =
////                        findViewById<ToggleButton>(R.id.renderimagetoggle).isActivated
////                    Log.e("#toggleGS12@@", global.renderimages.toString())
////                }
//
//                R.id.nav_account -> {
//                    Log.e("#toggleGS12@@", "my acc pressed")
//                }
//
//
//            }
//            true
//        }


        //Setting up the Recycler View
        adapter = NewsAdapter(this@MainActivity, articles)
        val rv = findViewById<RecyclerView>(R.id.newsList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this@MainActivity)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        getNews()


        // Scroll listener for Recycler View which sets the App Title to the source of news
        rv.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val tempPos = (rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if(tempPos>=0){
                    global.position = tempPos
                    var tempTitle = articles[global.position].source.name
                    if((tempTitle.length>4) && (tempTitle.substring(tempTitle.length-4, tempTitle.length) ==".com")){
                        tempTitle = tempTitle.substring(0, tempTitle.length -4)
                    }
                    title = tempTitle
                }
            }
        })

        // Search Button code
        searchbtn.setOnClickListener {
            val searchField = drawerLayout.findViewById<TextView>(R.id.searchfield)
            val searchstring: String = searchField.text.toString()
            if (searchstring.isNotEmpty()) {
                changeNews(searchstring)
                searchField.text = ""
                drawerLayout.close()
                rv.scrollToPosition(0)
                try {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    Log.d("OK", "Keyboard already closed")
                }
            }
        }

        val signoutbtn : Button = findViewById(R.id.SignOut)

        signoutbtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


    }



    private fun getNews() {
        val news: Call<News> = NewsService.newsInstance.getHeadlines("tech")

        news.enqueue(object : Callback<News> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
//                    Log.d("GS#123789@", news.toString())
                    articles.addAll(news.articles)
                    articles.reverse()
//                    articles.shuffle()
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("GS#123789@", "Can't fetch news", t)
            }
        })


    }


    private fun changeNews(searchnews:String) {
        val news: Call<News> = NewsService.newsInstance.getHeadlines(searchnews)

        news.enqueue(object : Callback<News> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
//                    Log.d("GS#123789@", news.toString())
                    articles.clear()
                    articles.addAll(news.articles)
//                    articles.reverse()
//                    articles.shuffle()
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("GS#123789@", "Can't fetch news", t)
            }
        })


    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }



}