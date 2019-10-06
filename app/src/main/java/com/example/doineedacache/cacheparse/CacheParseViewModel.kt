package com.example.doineedacache.cacheparse

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doineedacache.model.CacheEntry
import com.example.doineedacache.model.DayMonth
import com.example.doineedacache.repository.CachesFoundRepository
import kotlinx.coroutines.launch
import java.util.*

class CacheParseViewModel(val cachesFoundRepository: CachesFoundRepository): ViewModel() {

    var caches: MutableLiveData<List<CacheEntry>> = MutableLiveData()
    var missingDates: MutableLiveData<List<DayMonth>> = MutableLiveData()

    fun processCaches(uri: Uri) {
        Log.d("CPVM", "processCaches")
        viewModelScope.launch {
            val cacheList=cachesFoundRepository.getCaches(uri)
            caches?.postValue(cacheList)
            Log.d("CPVM", "processCaches launch - ${cacheList.size}")
        }
    }

    fun findMissingDates() {
        viewModelScope.launch {
            val calendarList = mutableListOf<DayMonth>()
            for (month in 1..12)
                for (day in 1..daysInMonth[month - 1])
                    calendarList.add(DayMonth(day, month))

            caches.value?.parallelStream()?.forEach { cacheEntry ->
                cacheEntry.dateFound?.run {
                    calendarList.remove(DayMonth(date, month + 1))
                    Log.d("CPVM", "Removing $date, $month")
                }
            }

            missingDates.postValue(calendarList)
        }
    }


    val daysInMonth = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
}

