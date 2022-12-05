package com.example.networkingpr


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://newsapi.org/v2/top-headlines?country=in&apiKey=302a4f6105b34fd8b4b0303f91cc149c
//https://newsapi.org/v2/everything?q=tesla&from=2022-10-14&sortBy=publishedAt&apiKey=302a4f6105b34fd8b4b0303f91cc149c

const val BASE_URL = "https://newsapi.org/"
const val API_KEY = "302a4f6105b34fd8b4b0303f91cc149c"
interface NewsInterface{

    @GET("v2/everything?apiKey=$API_KEY")
    fun getHeadlines(@Query("q") query: String):Call<News>

}

object NewsService{
    val newsInstance: NewsInterface
    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        newsInstance = retrofit.create(NewsInterface::class.java)
    }


}






//https://newsapi.org/v2/https://newsapi.org/v2/everything?q=tech&apiKey=302a4f6105b34fd8b4b0303f91cc149c

//https://newsapi.org/v2/top-headlines?apiKey=302a4f6105b34fd8b4b0303f91cc149c