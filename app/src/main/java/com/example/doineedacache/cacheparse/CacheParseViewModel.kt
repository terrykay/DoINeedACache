package uk.themeadow.doineedacache.cacheparse

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.Complete
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.Loading
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.MviValues
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.ShowChooser
import uk.themeadow.doineedacache.model.CacheEntry
import uk.themeadow.doineedacache.model.DayMonth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.themeadow.doineedacache.repository.CachesFoundRepository

class CacheParseViewModel(val cachesFoundRepository: CachesFoundRepository): ViewModel() {

    val caches: MutableLiveData<List<CacheEntry>> = MutableLiveData()
    val missingDates: MutableLiveData<List<DayMonth>> = MutableLiveData()
    val viewState: MutableLiveData<MviValues> = MutableLiveData()
    var job: Job? = null

    fun loadCaches(ignoreCache: Boolean = true) {
        var local: List<CacheEntry>? = null
        if (!ignoreCache) local = cachesFoundRepository.getLocalCaches()
        if (local!=null) {
            caches.value = local
            findMissingDates()
        } else
        viewState.postValue(ShowChooser())
    }

    fun processCaches(uri: Uri) {
        Log.d("CPVM", "processCaches")
        viewState.postValue(Loading())
        job = viewModelScope.launch {
            val cacheList = cachesFoundRepository.getCaches(uri)
            caches?.postValue(cacheList)
            Log.d("CPVM", "processCaches launch - ${cacheList.size}")
            viewState.postValue(Complete())
        }
    }

    fun findMissingDates() {
        viewState.postValue(Loading())
        job = viewModelScope.launch {
            val calendarList = mutableListOf<DayMonth>()
            for (month in 1..12)
                for (day in 1..daysInMonth[month - 1])
                    calendarList.add(DayMonth(day, month))

            caches.value?.parallelStream()?.forEach { cacheEntry ->
                cacheEntry.dateFound?.run {
                    calendarList.remove(DayMonth(date, month + 1))
                }
            }

            cachesFoundRepository.getIgnoreList()?.forEach {  dayMonth ->
                calendarList.remove(dayMonth)
                Log.d("CPVM", "Ignore list - $dayMonth")
            }

            missingDates.postValue(calendarList)
            viewState.postValue(Complete())
        }
    }

    override fun onCleared() {
        job?.cancel()
    }

    val daysInMonth = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
}

