package com.example.redditaddon.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditaddon.R
import com.example.redditaddon.databinding.ActivityMainBinding
import com.example.redditaddon.utils.RecyclerAdapter
import com.example.redditaddon.viewmodels.MainActivityViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

const val REQUEST_CODE = 1
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var adapter: RecyclerAdapter? = null
    private lateinit var manager: LinearLayoutManager
    private lateinit var myViewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        title = "reddit top"

        binding.lifecycleOwner = this

        initAdapter()

        initViewModel()

        with(binding) {
            scaledImg.setOnClickListener {
                scaledLay.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            }

            backToTop.setOnClickListener {
                manager.scrollToPosition(0)
            }

            saveButton.setOnClickListener {
                if (ContextCompat.checkSelfPermission(baseContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else {
                    ActivityCompat.requestPermissions(parent,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE
                    )
                }
                scaledLay.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            }
        }
    }

    private fun initAdapter() {
        manager = LinearLayoutManager(this)

        adapter = RecyclerAdapter(binding)

        with(binding) {
            articles.layoutManager = manager

            articles.adapter = adapter

            articles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItem = manager.itemCount
                    val lastVisibleItem = manager.findLastVisibleItemPosition()

                    if (manager.findFirstVisibleItemPosition() > 4) {
                        binding.backToTop.visibility = View.VISIBLE
                    } else {
                        binding.backToTop.visibility = View.GONE
                    }

                    if (totalItem <= lastVisibleItem + totalItem / 2){
                        adapter!!.loadMore?.onLoadMore()
                    }
                }
            })

            adapter!!.setLoadMore(object : RecyclerAdapter.MyLoadMore {
                override fun onLoadMore() {
                    val after = adapter!!.publications.lastOrNull()!!.data.name
                    fillList(after)
                }
            })
        }
    }

    private fun initViewModel() {
        myViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        myViewModel.getAllPublications()
        myViewModel.observePublicationData().observe(this) {
            adapter!!.updateAdapter(it)
        }
    }

    private fun fillList(after: String) {
        adapter!!.publications.lastOrNull()
        myViewModel.uploadPublications(after)
        myViewModel.observePublicationData().observe(this) {
            adapter!!.updateAdapter(it)
        }
    }


    private fun saveImage() {
        val bitmap = findViewById<ImageView>(R.id.scaledImg).drawToBitmap()
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageInQ(bitmap)
        } else {
            saveImageInLegacy(bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageInQ(bitmap: Bitmap): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = application.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }

        return imageUri
    }

    private fun saveImageInLegacy(bitmap:Bitmap) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        val fos = FileOutputStream(image)
        fos.use {bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)}
        fos.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    saveImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(this, "Without this permission we can't load the image", Toast.LENGTH_LONG).show()
        }
    }
}