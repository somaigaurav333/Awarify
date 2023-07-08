package com.example.networkingpr

//import com.example.Awarify.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    lateinit var rv: RecyclerView
    lateinit var shimmer: ShimmerFrameLayout
    lateinit var refreshView: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //Setting up the Recycler View
        adapter = NewsAdapter(this.requireContext(), articles)
        rv = view.findViewById(R.id.newsList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this.context)
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmer = view.findViewById(R.id.shimmerLayoutHome)
        refreshView = view.findViewById(R.id.refresh)
        refreshView.setColorSchemeColors(Color.parseColor("#993399"))

        rv.visibility = View.GONE
        getNews()



        refreshView.setOnRefreshListener {
            rv.visibility = View.GONE
            getNews()
        }


        // Scroll listener for Recycler View which sets the App Title to the source of news
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val tempPos =
                    (rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (tempPos >= 0) {
                    global.position = tempPos
                    var tempTitle = articles[global.position].source.name
                    if ((tempTitle.length > 4) && (tempTitle.substring(
                            tempTitle.length - 4, tempTitle.length
                        ) == ".com")
                    ) {
                        tempTitle = tempTitle.substring(0, tempTitle.length - 4)
                    }
                    global._tempTitle = tempTitle

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
                    articles.addAll(news.articles)
                    articles.reverse()
                    adapter.notifyDataSetChanged()
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                    refreshView.isRefreshing = false

                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {

                Log.d("GS#123789@", "Unable to fetch news", t)
                Toast.makeText(context, "Error: Couldn't fetch News", Toast.LENGTH_SHORT).show()
                refreshView.isRefreshing = false
                if ((rv.adapter?.itemCount!! > 0) || (rv.visibility == View.VISIBLE)) {
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    rv.visibility = View.VISIBLE
                }
            }

        })


    }


}