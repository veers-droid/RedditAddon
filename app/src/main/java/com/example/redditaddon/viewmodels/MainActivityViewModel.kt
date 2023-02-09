package com.example.redditaddon.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.redditaddon.model.Children
import com.example.redditaddon.model.Publication
import com.example.redditaddon.retrofit.client.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivityViewModel: ViewModel() {

    var publicationsLiveData = MutableLiveData<MutableList<Children>>()

    fun getAllPublications() {
        RetrofitClient.client.getTopPublications().enqueue(object : Callback<Publication> {
            override fun onResponse(call: Call<Publication>, response: Response<Publication>) {
                if (response.body() != null) {
                    publicationsLiveData.value = response.body()!!.data.children as MutableList<Children>
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<Publication>, t: Throwable) {
                Log.d("TAG",t.message.toString())
            }
        })
    }


    fun uploadPublications(after: String ) {
        RetrofitClient.client.uploadMorePublications(after).enqueue(object : Callback<Publication> {
            override fun onResponse(call: Call<Publication>, response: Response<Publication>) {
                if (response.body() != null) {
                    publicationsLiveData.value?.clear()
                    publicationsLiveData.value?.addAll(response.body()!!.data.children)
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<Publication>, t: Throwable) {
                Log.d("TAG",t.message.toString())
            }
        })
    }

    fun observePublicationData(): LiveData<MutableList<Children>> {
        return publicationsLiveData
    }
}