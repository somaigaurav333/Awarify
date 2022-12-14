package com.example.networkingpr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide


class NewsAdapter(private val context: Context, private val articles: List<Article>) :
    Adapter<NewsAdapter.ArticleViewHolder>() {




    class ArticleViewHolder(itemView: View) : ViewHolder(itemView) {
        var newsImage = itemView.findViewById<ImageView>(R.id.newsImage)!!
        var newsTitle = itemView.findViewById<TextView>(R.id.newsTitle)!!
        var newsDescription = itemView.findViewById<TextView>(R.id.newsDescription)!!
        var likebtn = itemView.findViewById<Button>(R.id.likebtn)!!
        var liked = false
        var sharebtn = itemView.findViewById<Button>(R.id.sharebtn)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        global.position++
        return ArticleViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]



        holder.likebtn.setOnClickListener {
            if(!holder.liked){
                holder.likebtn.text = "Liked!"
                holder.liked = true
            }else{
                holder.likebtn.text = "Like"
                holder.liked = false
            }

        }

        holder.sharebtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, article.url)
            context.startActivity(Intent.createChooser(shareIntent, "Share link via"))

        }

        holder.newsTitle.text = article.title
        holder.newsDescription.text = article.description

        if(global.renderimages){
            holder.newsImage.visibility = View.VISIBLE
            Glide.with(context).load(article.urlToImage ).placeholder(R.drawable.ic_baseline_android_loading).error(R.drawable.ic_baseline_android_error).into(holder.newsImage)
        }else{
            holder.newsImage.visibility = View.GONE
        }



        holder.itemView.setOnClickListener {
            Toast.makeText(context, article.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("URL",article.url)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return articles.size
    }




}