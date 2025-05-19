package com.example.redditaddon.activities.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.redditaddon.App
import com.example.redditaddon.app.dagger.daggerViewModel
import com.example.redditaddon.model.PublicationItem
import com.example.redditaddon.navigation.AppNavGraph
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContent {
            MaterialTheme {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        AppNavGraph(navController, viewModelFactory)
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModelFactory: ViewModelProvider.Factory) {
    val viewModel = daggerViewModel<MainActivityViewModel>(viewModelFactory)
    val publications = viewModel.pagedPublications.collectAsLazyPagingItems()
    Log.d("START", "viewmodel request end")
    var showScaledImage by remember { mutableStateOf(false) }
    var scaledImageUrl by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("RedditAddon") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { /* TODO: Open drawer */ }
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                navController.navigate("profile")
                            }
                    )
                }
            )
        },
        floatingActionButton = {
            if (remember { derivedStateOf { listState.firstVisibleItemIndex > 4 } }.value) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Back to top")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (showScaledImage) {
                ScaledImageView(imageUrl = scaledImageUrl) {
                    showScaledImage = false
                }
            } else {
                LazyColumn(state = listState) {
                    items(publications.itemCount) { index ->
                        val post = publications[index]
                        PublicationItemView(post = post!!) {
                            scaledImageUrl = post.thumbNail.image
                            showScaledImage = true
                        }
                    }
                }


                when (publications.loadState.append) {
                    is LoadState.Loading -> {
                        Text("Загрузка...")
                    }
                    is LoadState.Error -> {
                        val e = publications.loadState.append as LoadState.Error
                        Text("Ошибка: ${e.error.message}")
                    }
                    else -> {}
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${post.commentsCount} comments",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = countPostTime(post.created),
                style = MaterialTheme.typography.bodySmall
            )
        }
        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(top = 5.dp))
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
