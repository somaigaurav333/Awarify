package com.example.networkingpr

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExploreFragment : Fragment() {

    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var rv: RecyclerView
    var searchtext: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_explore, container, false)

        adapter = NewsAdapter(this.requireContext(), articles)
        rv = view.findViewById(R.id.explorerv)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this.context)
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getNews()

        val searchEditText: EditText = view.findViewById(R.id.exploresearchtextview)

        searchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                searchtext = searchEditText.text.toString()


                if (searchtext.isNotEmpty()) {
                    if (searchtext.isNotEmpty()) {
                        searchNews(searchtext)
                        rv.scrollToPosition(0)
                    }
                }

                try {

                    hideKeyboard()

                } catch (e: Exception) {
                    Log.d("OK", "Keyboard already closed")
                }
            }
            true
        }


    }


    private fun searchNews(searchnews: String) {
        val news: Call<News> = NewsService.newsInstance.getHeadlines(searchnews)

        news.enqueue(object : Callback<News> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    articles.clear()
                    articles.addAll(news.articles)

                    adapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("GS#123789@", "Can't fetch news", t)
            }
        })


    }


    private fun getNews() {
        val news: Call<News> = NewsService.newsInstance.getHeadlines("Universe")

        news.enqueue(object : Callback<News> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news = response.body()
                if (news != null) {
                    articles.addAll(news.articles)
                    articles.reverse()
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("GS#123789@", "Can't fetch news", t)
            }
        })


    }


    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}