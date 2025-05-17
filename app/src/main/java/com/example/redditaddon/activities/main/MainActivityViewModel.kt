package com.example.redditaddon.activities.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.repositories.news.NewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
    private val repository : NewsRepository
): ViewModel() {

    private val _publicationsLiveData = MutableLiveData<List<PublicationItem>>()
    val publicationsLiveData : LiveData<List<PublicationItem>>
        get() = _publicationsLiveData

    fun getAllPublications() {
        Log.d("START", "viewmodel start request")
        viewModelScope.launch {
            Log.d("START", "viewmodel end request")
            _publicationsLiveData.value = repository.getPubs()
        }
    }

    fun uploadPublications(after: String ) {
        viewModelScope.launch {
            val pubs = repository.updatePubs(after)
            if (pubs.isNotEmpty()) {
                _publicationsLiveData.value = pubs
            }
        }
    }
}