package uk.themeadow.doineedacache.calendar.ui.calendar

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import uk.themeadow.doineedacache.model.DayMonth
import kotlinx.android.synthetic.main.calendar_fragment.*
import org.koin.android.scope.currentScope
import uk.themeadow.doineedacache.R
import uk.themeadow.doineedacache.cacheparse.CalendarViewModelFactory
import uk.themeadow.doineedacache.com.example.doineedacache.repository.CachesFoundRepositoryImpl
import uk.themeadow.doineedacache.repository.SettingsRepository
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {

    var dateList: java.util.ArrayList<DayMonth> = arrayListOf<DayMonth>()
    lateinit var calendarViewModel: CalendarViewModel
    val localStorage: SettingsRepository by currentScope.inject()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dateList = arguments?.getParcelableArrayList<DayMonth>(DATES)!!
        calendarViewModel = ViewModelProviders.of(this, CalendarViewModelFactory(context!!, CachesFoundRepositoryImpl(context!!, localStorage)))
            .get(CalendarViewModel::class.java)
        calendarViewModel.eventList.observe (this, Observer { list -> calendarView.setEvents(list) } )
        return inflater.inflate(
            uk.themeadow.doineedacache.R.layout.calendar_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView.setOnDayClickListener { eventDay ->
            AlertDialog.Builder(ContextThemeWrapper(context!!, R.style.CustomAlertDialogTheme))
                .setTitle(R.string.remove_entry_title)
                .setMessage(R.string.remove_entry_body)
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        calendarViewModel.remove(eventDay)
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                        dialog.dismiss()
                    })
                .create().show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        calendarViewModel.processDates(dateList)
    }

    fun getEventListSize(): Int = calendarViewModel.getEventListSize()
}
