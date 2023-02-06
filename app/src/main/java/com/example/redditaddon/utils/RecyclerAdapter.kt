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

        val context = holder.itemView.context



        with(holder.binding) {
            author = publications[position].data.subreddit_name_prefixed
            time = publications[position].data.created_utc.toString()
            text = publications[position].data.title

            comments = publications[position].data.num_comments.toString() + "comments"

            //setting up the photo via Picasso library
            Picasso.with(context)
                .load(thumbNail)
                .into(image)

            //if tap on the image open it on fullscreen
            image.setOnClickListener {
                Picasso.with(context)
                    .load(thumbNail)
                    .into(mainBind.scaledImg)

                mainBind.scaledLay.visibility = View.VISIBLE
            }

        }

    }

    override fun getItemCount(): Int = publications.size


    inner class MyViewHolder (val binding: RecycleviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}