package uk.themeadow.doineedacache

import android.util.Log
import uk.themeadow.doineedacache.repository.SettingsRepository
import java.util.*

class MainPresenterImpl(val settingsRepository: SettingsRepository): MainContract.Presenter {

    var view: MainContract.View? = null

    override fun checkIfFirstRun() {
        Log.d("MP", "${settingsRepository.get(FIRST_RUN_KEY)}")
        if (settingsRepository.get(FIRST_RUN_KEY)==null) {
            settingsRepository.save(FIRST_RUN_KEY, Date().toString())
            view?.showHelp()
        }
    }

    override fun bind(view: MainContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    private val FIRST_RUN_KEY = "FIRSTRUN"
}

interface MainContract{
    interface View {
        fun showHelp()
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun checkIfFirstRun()
    }
}