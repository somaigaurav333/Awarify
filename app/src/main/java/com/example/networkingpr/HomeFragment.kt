package com.example.networkingpr

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.Awarify.R
import com.example.networkingpr.R
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class HomeFragment : Fragment() {

    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var rv : RecyclerView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
        //Setting up the Recycler View
        adapter = NewsAdapter(this.requireContext(), articles)
        rv = view.findViewById<RecyclerView>(R.id.newsList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this.context)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
//                    global._tempTitle.value = tempTitle


                }
            }
        })

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

}