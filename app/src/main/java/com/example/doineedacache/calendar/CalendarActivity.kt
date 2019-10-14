package uk.themeadow.doineedacache.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_help.*
import uk.themeadow.doineedacache.MainActivity
import uk.themeadow.doineedacache.R
import uk.themeadow.doineedacache.calendar.ui.calendar.CalendarFragment
import uk.themeadow.doineedacache.help.HelpActivity
import uk.themeadow.doineedacache.model.DayMonth
import kotlinx.android.synthetic.main.calendar_activity.*
import kotlinx.android.synthetic.main.calendar_activity.toolbar
import uk.themeadow.doineedacache.help.HelpActivity.Companion.EVENT_SIZE_TAG

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CalendarFragment.newInstance(intent.getParcelableArrayExtra(DATES).toList() as List<DayMonth>),
                    CALENDAR_FRAGMENT
                )
                .commitNow()
        }
        setSupportActionBar(toolbar)
        getSupportActionBar()?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_help -> {
                val fragment: CalendarFragment = supportFragmentManager.findFragmentByTag(CALENDAR_FRAGMENT) as CalendarFragment
                val intent = Intent(this, HelpActivity::class.java).apply {
                    fragment?.let {
                        putExtra(EVENT_SIZE_TAG, it.getEventListSize())
                    }
                }
                startActivity(intent)
            }
            R.id.menu_refresh -> {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.REFRESH_KEY, true)
                })
                finish()
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    companion object {
        const val DATES = "dates"
        const val CALENDAR_FRAGMENT = "calendarFragment"
    }
}
