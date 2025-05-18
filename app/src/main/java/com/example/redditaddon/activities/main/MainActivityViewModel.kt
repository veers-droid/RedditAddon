package com.example.redditaddon.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.retrofit.repositories.news.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
    repository : NewsRepository
): ViewModel() {

    val pagedPublications: Flow<PagingData<PublicationItem>> =
        repository.getPagedPublications()
            .flow
            .cachedIn(viewModelScope)
}