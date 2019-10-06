package com.example.doineedacache.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doineedacache.R
import com.example.doineedacache.calendar.ui.calendar.CalendarFragment
import com.example.doineedacache.model.DayMonth

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CalendarFragment.newInstance(intent.getParcelableArrayExtra(DATES).toList() as List<DayMonth>))
                .commitNow()
        }

    }

    companion object {
        val DATES = "dates"
    }
}
