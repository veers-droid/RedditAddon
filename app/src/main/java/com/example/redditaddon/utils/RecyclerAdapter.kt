package com.example.redditaddon.utils

import android.annotation.SuppressLint
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

class RecyclerAdapter (
    private val mainBind: ActivityMainBinding
) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    var publications: List<Children> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleviewItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thumbNail = publications[position].data.thumbnail
        val thumbNailHeight = publications[position].data.thumbnail_height * 5
        val thumbNailWidth = publications[position].data.thumbnail_width * 5

        val postTime = countPostTime(position)

        val context = holder.itemView.context

        with(holder.binding) {
            author = publications[position].data.subreddit_name_prefixed
            time = postTime
            text = publications[position].data.title

            comments = publications[position].data.num_comments.toString() + " comments"

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

    private fun countPostTime(position: Int): String {
        val curDate = Date.from(Instant.now())
        val curDataFormatted =  android.text.format.DateFormat.format("MMMM, dd, yyyy HH:mm:ss", curDate).toString()

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = (publications[position].data.created_utc * 1000L).toLong()
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


    inner class MyViewHolder (val binding: RecycleviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}