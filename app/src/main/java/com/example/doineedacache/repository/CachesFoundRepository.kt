package uk.themeadow.doineedacache.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import uk.themeadow.doineedacache.model.CacheEntry
import uk.themeadow.doineedacache.model.DayMonth
import uk.themeadow.doineedacache.util.FileChooser
import com.google.gson.Gson
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.START_DOCUMENT
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry


class CachesFoundRepositoryImpl(val context: Context, val localStorage: SettingsRepository) :
    CachesFoundRepository {

    val dateFormatter by lazy { SimpleDateFormat(dateFormat) }
    val gson by lazy { Gson() }

    override fun getLocalCaches(): List<CacheEntry>? {
        val caches = localStorage.get(CACHES)
        if (caches != null)
            return Gson().fromJson(caches, CacheList::class.java).dates
        else
            return null
    }

    private fun saveLocalCaches(cacheList: List<CacheEntry>) {
        localStorage.save(CACHES, Gson().toJson(CacheList(cacheList)))
    }

    override fun getCaches(uri: Uri): List<CacheEntry> {
        return readFile(uri)
    }

    fun readFile(uri: Uri): List<CacheEntry> {
        var inputStream = context.contentResolver.openInputStream(uri)
        val zipInputStream = ZipInputStream(inputStream)
        var entry: ZipEntry? = zipInputStream.nextEntry
        if (entry == null) {
            inputStream?.close()
            inputStream = context.contentResolver.openInputStream(uri)
        } else
            inputStream = zipInputStream

        val cacheList = parse(inputStream!!)
        saveLocalCaches(cacheList)
        return cacheList
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
            tag = parser.name
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
                        "groundspeak:date" -> if (parsingLog) tempDate =
                            dateFormatter.parse(text)

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

    override fun getIgnoreList(): List<DayMonth>? {
        val get = localStorage.get(IGNORE_LIST)
        Log.d("CFR", "ignore list = $get")
        return gson.fromJson(
            get ?: "{}",
            CachesFoundRepositoryImpl.IgnoreList::class.java
        ).dates
    }

    private fun setIgnoreList(dateList: List<DayMonth>) {
        localStorage.save(IGNORE_LIST, gson.toJson(IgnoreList(dateList)))
    }

    override fun remove(cacheEntry: CacheEntry) {
        // As cache list is a list of all caches, we instead create a listof 'To ignore' dates
        cacheEntry.dateFound?.let {
            val dayMonth = DayMonth(it)
            getIgnoreList().orEmpty().toMutableList().run {
                add(dayMonth)
                setIgnoreList(this)
            }
        }
    }

    override fun clearIgnoreList() {
        setIgnoreList(listOf())
    }

    fun addHoursToDate(date: Date, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.time
    }

    companion object {
        val PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        const val filename = ".gpx"
        const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        const val CACHES = "caches"
        const val IGNORE_LIST = "ignore_list"
    }

    data class CacheList(val dates: List<CacheEntry>)
    data class IgnoreList(val dates: List<DayMonth>)
}

interface CachesFoundRepository {
    fun getLocalCaches(): List<CacheEntry>?
    fun getCaches(uri: Uri): List<CacheEntry>
    fun remove(cacheEntry: CacheEntry)
    fun getIgnoreList(): List<DayMonth>?
    fun clearIgnoreList()
}