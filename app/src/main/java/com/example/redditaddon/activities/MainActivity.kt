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
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redditaddon.R
import com.example.redditaddon.databinding.ActivityMainBinding
import com.example.redditaddon.model.Children
import com.example.redditaddon.model.Publication
import com.example.redditaddon.retrofit.client.RetrofitClient
import com.example.redditaddon.retrofit.services.RetrofitService
import com.example.redditaddon.utils.RecyclerAdapter
import retrofit2.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

const val REQUEST_CODE = 1
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var retrofitService: RetrofitService? = null
    var adapter: RecyclerAdapter? = null
    private var publicationsList: MutableList<Children> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        title = "reddit top"

        retrofitService = RetrofitClient.client.create(RetrofitService::class.java)


        binding.articles!!.adapter = adapter

        adapter = RecyclerAdapter(binding)


        val manager = LinearLayoutManager(this)

        with(binding) {

            articles.layoutManager = manager
            articles.adapter = adapter

            scaledImg.setOnClickListener {
                scaledLay.visibility = View.GONE
                mainView.visibility = View.GONE
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
                binding.scaledLay.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
            }
        }
        getPublications()

    }

    private fun getPublications() {
        val call = retrofitService!!.getTopPublications()
        call.enqueue(object : Callback<Publication> {
            override fun onResponse(
                call: Call<Publication>,
                response: Response<Publication>
            ) {
                Log.d("tag", "Total pubs: " + response.body()!!.data.children[0].data.subreddit_name_prefixed)
                val pubs = response.body()
                if (pubs != null) {
                    publicationsList.addAll(pubs.data.children)
                    adapter!!.publications = publicationsList
                    adapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Publication>, t: Throwable) {
                Log.e("tag", "Got error: " + t.localizedMessage)
            }
        })
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