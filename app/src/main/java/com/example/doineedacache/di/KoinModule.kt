package uk.themeadow.doineedacache.di

import uk.themeadow.doineedacache.MainActivity
import uk.themeadow.doineedacache.MainContract
import uk.themeadow.doineedacache.MainPresenterImpl
import uk.themeadow.doineedacache.repository.SettingsRepository
import uk.themeadow.doineedacache.repository.SettingsRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uk.themeadow.doineedacache.calendar.ui.calendar.CalendarFragment

val koinModule = module {
    single <SettingsRepository> { SettingsRepositoryImpl(androidContext()) }

    single <MainContract.Presenter> { MainPresenterImpl(get()) }

    // Scoped MyScopePresenter instance
    scope(named<MainActivity>()) {
        scoped { MainPresenterImpl(get()) }
    }

    scope(named<CalendarFragment>()) {
        scoped { SettingsRepositoryImpl(androidContext()) }
    }
}