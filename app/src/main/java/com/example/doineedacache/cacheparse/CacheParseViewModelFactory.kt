package uk.themeadow.doineedacache.cacheparse

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.themeadow.doineedacache.calendar.ui.calendar.CalendarViewModel
import uk.themeadow.doineedacache.repository.CachesFoundRepository

class CacheParseViewModelFactory(val context: Context, val cachesFoundRepository: CachesFoundRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CacheParseViewModel(cachesFoundRepository) as T
        }
    }

class CalendarViewModelFactory(val context: Context, val cachesFoundRepository: CachesFoundRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CalendarViewModel(cachesFoundRepository) as T
    }
}