package com.example.doineedacache.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.doineedacache.model.CacheEntry
import com.example.doineedacache.util.FileChooser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.START_DOCUMENT
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry


class CachesFoundRepositoryImpl(val context: Context) : CachesFoundRepository {

    val dateFormatter by lazy { SimpleDateFormat(dateFormat) }

    override fun getCaches(uri: Uri): List<CacheEntry> {
        return readFile(uri)
    }

    fun readFile(uri: Uri): List<CacheEntry> {
        var inputStream = context.contentResolver.openInputStream(uri)
        if (uri.toString().endsWith(".zip", ignoreCase = true)) {
            inputStream = ZipInputStream(inputStream)
            var entry: ZipEntry = inputStream.nextEntry
            while (entry!=null && entry.name.endsWith(".gpx", ignoreCase = true))
                entry = inputStream.nextEntry
            inputStream.read(ByteArray(2))
        }

        return parse(inputStream!!)
    }

    fun parse(inputStream: InputStream): List<CacheEntry> {
        val parserFactory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
        val parser: XmlPullParser = parserFactory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        var tag: String? = null
        var text = ""
        val cacheList = mutableListOf<CacheEntry>()
        var cache = CacheEntry()
        var parsingLog = false
        lateinit var tempDate: Date

        var event = parser.getEventType()
        while (event != XmlPullParser.END_DOCUMENT) {
            Log.d("CFR", "Event = $event")
            tag = parser.name
            Log.d("CFR", "Tag = $tag")
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (tag) {
                        "wpt" -> cache = CacheEntry()
                        "groundspeak:log" -> parsingLog = true
                    }
                }

                XmlPullParser.TEXT -> {
                    text = parser.getText();
                }

                XmlPullParser.END_TAG -> {
                    when (tag) {
                        "groundspeak:date" -> if (parsingLog) tempDate = dateFormatter.parse(text)

                        "groundspeak:name" -> cache.name = text
                        "groundspeak:log" -> {
                            parsingLog = false
                            cache.dateFound = addHoursToDate(tempDate, -4)
                        }

                        "wpt" -> cacheList.add(cache)
                    }
                }
            }
            event = parser.next();

        }

        return cacheList
    }

    fun addHoursToDate(date: Date, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.time
    }

    companion object {
        val PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filename = ".gpx"
        val dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    }
}

interface CachesFoundRepository {
    fun getCaches(uri: Uri): List<CacheEntry>
}