package com.example.doineedacache.calendar.ui.calendar

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applandeo.materialcalendarview.EventDay
import com.example.doineedacache.R
import com.example.doineedacache.model.DayMonth
import kotlinx.android.synthetic.main.calendar_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {

    var dateList: java.util.ArrayList<DayMonth> = arrayListOf<DayMonth>()

    companion object {
        fun newInstance(notFoundDays: List<DayMonth>) = CalendarFragment()
            .apply {
                arguments = Bundle().apply {
                    val list: ArrayList<DayMonth> = ArrayList()
                    list.addAll(notFoundDays)
                    putParcelableArrayList(DATES, list)
                }
            }

        val DATES = "dates"
    }

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dateList = arguments?.getParcelableArrayList<DayMonth>(DATES)!!
        return inflater.inflate(com.example.doineedacache.R.layout.calendar_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val eventList: MutableList<EventDay> = mutableListOf()
        val calList: MutableList<Calendar> = mutableListOf()

        dateList.forEach { dayMonth ->
            val instance = Calendar.getInstance()

            instance.set(Calendar.DAY_OF_MONTH, dayMonth.day)
            instance.set(Calendar.MONTH, dayMonth.month)
            eventList.add(EventDay(instance, R.drawable.ic_arrow_right))
            calList.add(instance)

            Log.d("CF", "cal = $instance")
        }

        calendarView.setEvents(eventList)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView
    }
}
