package com.example.doineedacache.cacheparse

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doineedacache.repository.CachesFoundRepository

class CacheParseViewModelFactory(val context: Context, val cachesFoundRepository: CachesFoundRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CacheParseViewModel(cachesFoundRepository) as T
        }
    }