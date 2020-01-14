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


interface CachesFoundRepository {
    fun getLocalCaches(): List<CacheEntry>?
    fun getCaches(uri: Uri): List<CacheEntry>
    fun remove(cacheEntry: CacheEntry)
    fun getIgnoreList(): List<DayMonth>?
    fun clearIgnoreList()
}