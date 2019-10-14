package uk.themeadow.doineedacache

import android.app.Application
import uk.themeadow.doineedacache.di.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DoINeedACacheApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // Android context
            androidContext(this@DoINeedACacheApplication)
            // modules
            modules(koinModule)
        }
    }
}