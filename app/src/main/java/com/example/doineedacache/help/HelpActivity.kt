package uk.themeadow.doineedacache.help

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import uk.themeadow.doineedacache.MainActivity
import uk.themeadow.doineedacache.R
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_help.*
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_help.toolbar
import kotlinx.android.synthetic.main.calendar_activity.*


class HelpActivity : AppCompatActivity() {

    var numberOfEvents: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setSupportActionBar(toolbar)
        getSupportActionBar()?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        numberOfEvents = intent.getIntExtra(EVENT_SIZE_TAG, -1)
        if (numberOfEvents >= 0) {
            cacheCountTextView.text = getString(R.string.caches_needed).format(numberOfEvents)
            cacheCountTextView.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {
        const val EVENT_SIZE_TAG = "EventTag"
    }
}
