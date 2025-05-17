package com.example.redditaddon.activities.main

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.redditaddon.App
import com.example.redditaddon.model.PublicationItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MainActivityCompose : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val mainViewModel : MainActivityViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("START", "")
        App.appComponent.inject(this)
        Log.d("START", "inject")
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }



    @Composable
    fun MainScreen() {
        val publications by mainViewModel.publicationsLiveData.observeAsState(emptyList())
        mainViewModel.getAllPublications()
        Log.d("START", "viewmodel request end")
        var showScaledImage by remember { mutableStateOf(false) }
        var backToTopVisible by remember { mutableStateOf(false) }
        var scaledImageUrl by remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            if (showScaledImage) {
                ScaledImageView(imageUrl = scaledImageUrl) {
                    showScaledImage = false
                }
            } else {
                LazyColumn(state = listState) {
                    items(publications) { post ->
                        PublicationItemView(post = post) {
                            scaledImageUrl = post.thumbNail.image ?: ""
                            showScaledImage = true
                        }
                    }
                }
            }

            if (remember { derivedStateOf { listState.firstVisibleItemIndex } }.value > 4) {
                BackToTopButton {
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.animateScrollToItem(0)
                    }
                }
            }

        }
    }

    @Composable
    fun PublicationItemView(post: PublicationItem, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
//            Text(text = post.subredditNamePrefixed, style = MaterialTheme.typography.labelMedium)
            Text(text = post.text, style = MaterialTheme.typography.titleMedium)
            Text(text = "${post.commentsCount} comments", style = MaterialTheme.typography.bodySmall)
            Text(text = countPostTime(post.created), style = MaterialTheme.typography.bodySmall)

            if (post.thumbNail.image.endsWith(".jpg")) {
                AsyncImage(
                    model = post.thumbNail.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { if (post.thumbNail.image.endsWith(".jpg")) onClick() },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    private fun countPostTime(created: Double): String {
        val curDate = Date.from(Instant.now())
        val curDataFormatted =  android.text.format.DateFormat.format("MMMM, dd, yyyy HH:mm:ss", curDate).toString()

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = (created * 1000L).toLong()
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

    @Composable
    fun ScaledImageView(imageUrl: String, onClose: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClose() },
                contentScale = ContentScale.Fit
            )
        }
    }

    @Composable
    fun BackToTopButton(onClick: () -> Unit) {
        Box {
            FloatingActionButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Back to top")
            }
        }

    }
}