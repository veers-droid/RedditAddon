package com.example.redditaddon.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.redditaddon.databinding.ActivityMainBinding
import com.example.redditaddon.databinding.RecycleviewItemBinding
import com.example.redditaddon.model.Children
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter (
    private val mainBind: ActivityMainBinding
) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    var publications: MutableList<Children> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    var loadMore: MyLoadMore? = null

    @JvmName("setLoadMore1")
    fun setLoadMore(loadMore: MyLoadMore?) {
        this.loadMore = loadMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleviewItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.drawItem(publications[position])
    }

    private fun countPostTime(item: Children): String {
        val curDate = Date.from(Instant.now())
        val curDataFormatted =  android.text.format.DateFormat.format("MMMM, dd, yyyy HH:mm:ss", curDate).toString()

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = (item.data.created_utc * 1000L).toLong()
        val publicationTime: String = android.text.format.DateFormat.format("MMMM, dd, yyyy HH:mm:ss", calendar).toString()

        val dateFormatInput = DateTimeFormatter.ofPattern("MMMM, dd, yyyy HH:mm:ss")

        val curDateParsed = LocalDateTime.parse(curDataFormatted, dateFormatInput)
        val pubDateParsed = LocalDateTime.parse(publicationTime, dateFormatInput)

        val noOfHours = java.time.Duration.between(pubDateParsed, curDateParsed)

        return if(noOfHours.toHours() > 0) {
            noOfHours.toHours().toString() + " hours ago"
        } else {
            noOfHours.toHours().toString() + " minutes ago"
        }
    }

    override fun getItemCount(): Int = publications.size

    inner class MyViewHolder (private val binding: RecycleviewItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun drawItem(item: Children) {
            val thumbNail = item.data.thumbnail

            Log.d("author thumbNail", item.data.subreddit_name_prefixed + " " + thumbNail)

            val thumbNailHeight = item.data.thumbnail_height * 5
            val thumbNailWidth = item.data.thumbnail_width * 5

            val context = itemView.context
            val postTime = countPostTime(item)

            with(binding) {
                author = item.data.subreddit_name_prefixed
                text = item.data.title
                comments = item.data.num_comments.toString() + " comments"
                time = postTime

                if (thumbNail.endsWith(".jpg"))
                {
                    //setting up the photo via Picasso library
                    Picasso.with(context)
                        .load(thumbNail)
                        .noPlaceholder()
                        .resize(thumbNailWidth, thumbNailHeight)
                        .into(image)

                    //if tap on the image open it on fullscreen
                    image.setOnClickListener {
                        Picasso.with(context)
                            .load(thumbNail)
                            .into(mainBind.scaledImg)

                        mainBind.mainView.visibility = View.GONE
                        mainBind.scaledLay.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(publications: List<Children>) {
        this.publications.addAll(publications)
        notifyDataSetChanged()

    }

    interface MyLoadMore {
        fun onLoadMore()
    }
}
