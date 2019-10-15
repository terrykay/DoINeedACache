package uk.themeadow.doineedacache.calendar.ui.calendar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.EventDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.themeadow.doineedacache.R
import uk.themeadow.doineedacache.model.CacheEntry
import uk.themeadow.doineedacache.model.DayMonth
import uk.themeadow.doineedacache.repository.CachesFoundRepository
import java.util.*

class CalendarViewModel(val cachesFoundRepository: CachesFoundRepository) : ViewModel() {
    val eventList: MutableLiveData<MutableList<EventDay>> = MutableLiveData()
    var job: Job? = null
    // We add events for next year, so keep a mapping to allow removal of both
    val eventMap: MutableMap<EventDay, EventDay> = mutableMapOf()

    fun processDates(dateList: List<DayMonth>) {
        job = viewModelScope.launch {
            val eventListBuilder = mutableListOf<EventDay>()
            dateList.forEach { dayMonth ->
                var instance = Calendar.getInstance()
                instance.set(Calendar.DAY_OF_MONTH, dayMonth.day)
                instance.set(Calendar.MONTH, dayMonth.month - 1)
                val dayEvent = EventDay(instance, R.drawable.baseline_search_black_18dp)
                eventListBuilder.add(dayEvent)
                instance = instance.clone() as Calendar
                instance.add(Calendar.YEAR, 1)
                val dayEventNextYear = EventDay(instance, R.drawable.baseline_search_black_18dp)
                eventListBuilder.add(dayEventNextYear)

                eventMap[dayEvent] = dayEventNextYear
                eventMap[dayEventNextYear] = dayEvent
            }

            eventList.postValue(eventListBuilder)
        }
    }

    fun remove(event: EventDay) {
        eventList.value?.remove(event)
        eventList.value?.remove(eventMap[event])
        cachesFoundRepository.remove(CacheEntry(event.calendar.time))
        eventList.postValue(eventList.value)
    }

    fun getEventListSize(): Int = ((eventList.value?.size?:0) / 2)

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}