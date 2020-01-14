package uk.themeadow.doineedacache

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import uk.themeadow.doineedacache.cacheparse.CacheParseViewModel
import uk.themeadow.doineedacache.cacheparse.CacheParseViewModelFactory
import uk.themeadow.doineedacache.calendar.CalendarActivity
import uk.themeadow.doineedacache.calendar.CalendarActivity.Companion.DATES
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.Loading
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.MviValues
import uk.themeadow.doineedacache.uk.themeadow.doineedacache.model.ShowChooser
import uk.themeadow.doineedacache.help.HelpActivity
import uk.themeadow.doineedacache.model.CacheEntry
import uk.themeadow.doineedacache.model.DayMonth
import uk.themeadow.doineedacache.repository.SettingsRepository
import uk.themeadow.doineedacache.util.FileChooser.Companion.FILE_SELECT_CODE
import uk.themeadow.doineedacache.util.FileChooserImpl
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.scope.currentScope
import uk.themeadow.doineedacache.com.example.doineedacache.repository.CachesFoundRepositoryImpl

class MainActivity : BaseActivity(), MainContract.View {

    lateinit var cacheParseViewModel: CacheParseViewModel
    val localStorage: SettingsRepository by currentScope.inject()
    val contract: MainContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cacheParseViewModel = ViewModelProviders.of(this, CacheParseViewModelFactory(this, CachesFoundRepositoryImpl(applicationContext, localStorage)))
            .get(CacheParseViewModel::class.java)

        cacheParseViewModel.caches.observe(this, Observer { caches -> showCaches(caches) })
        cacheParseViewModel.missingDates.observe(this, Observer { missingDates -> showMissingDates(missingDates) })
        cacheParseViewModel.viewState.observe(this, Observer { state -> updateView(state) })

        contract.bind(this)
        Log.d("MA", "onCreate")
        cacheParseViewModel.loadCaches(intent.getBooleanExtra(REFRESH_KEY, false))
    }

    override fun onStart() {
        super.onStart()
        Log.d("MA", "onStart")
    }

    override fun showHelp() {
        Log.d("MA", "Show help")
        startActivity(Intent(this, HelpActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = data?.getData()
                    //    CachesFoundRepositoryImpl(fileChooser).getCaches()
                    Log.d("MA", "onActivityResult")
                    cacheParseViewModel.processCaches(uri!!)
                } else {
                    onBackPressed()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showCaches(caches: List<CacheEntry>) {
        Log.d("MA", "showCaches $caches")
        cacheParseViewModel.findMissingDates()
    }

    fun showMissingDates(missingDates: List<DayMonth>) {
        val intent = Intent(this, CalendarActivity::class.java).apply {
            putExtra(DATES, missingDates.toTypedArray())
        }
        startActivity(intent)                    //FileChooserImpl(this).getFilename("*/*")
                    //showHelp()
        finish()
    }

    private fun updateView(state: MviValues) {
        when (state) {
            is Loading -> busyView?.visibility = View.VISIBLE
            is ShowChooser -> {
                FileChooserImpl(this).getFilename("*/*")
                contract.checkIfFirstRun()
            }
            else -> busyView?.visibility = View.GONE
        }
    }

    companion object {
        const val REFRESH_KEY = "REFRESH"
    }
}
