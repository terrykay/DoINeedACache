package com.example.doineedacache

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.doineedacache.cacheparse.CacheParseViewModel
import com.example.doineedacache.cacheparse.CacheParseViewModelFactory
import com.example.doineedacache.cacheparse.DayMonth
import com.example.doineedacache.extensions.getPath
import com.example.doineedacache.model.CacheEntry
import com.example.doineedacache.repository.CachesFoundRepositoryImpl
import com.example.doineedacache.util.FileChooser.Companion.FILE_SELECT_CODE
import com.example.doineedacache.util.FileChooserImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors
import java.util.zip.ZipInputStream

class MainActivity : AppCompatActivity() {

    lateinit var cacheParseViewModel: CacheParseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cacheParseViewModel = ViewModelProviders.of(this, CacheParseViewModelFactory(this, CachesFoundRepositoryImpl(applicationContext)))
            .get(CacheParseViewModel::class.java)

        cacheParseViewModel.caches.observe(this, Observer { caches -> showCaches(caches) })
        cacheParseViewModel.missingDates.observe(this, Observer { missingDates -> showMissingDates(missingDates) })

        FileChooserImpl(this).getFilename("*/*")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> {
                Toast.makeText(this, "Done $resultCode", Toast.LENGTH_LONG).show()
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.getData()
                    //    CachesFoundRepositoryImpl(fileChooser).getCaches()
                    Log.d("MA", "onActivityResult")
                    cacheParseViewModel.processCaches(uri!!)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showCaches(caches: List<CacheEntry>) {
        Log.d("MA", "showCaches $caches")
        cacheParseViewModel.findMissingDates()
    }

    fun showMissingDates(missingDates: List<DayMonth>) {
        Log.d("MA", "missingDates $missingDates")
        missingDates.forEach { dayMonth ->
            Log.d("MA", "${dayMonth.day}, ${dayMonth.month}")
        }
    }
}
