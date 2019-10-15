package com.example.doineedacache

import android.util.Log
import io.mockk.*
import org.junit.Before
import org.junit.Test
import uk.themeadow.doineedacache.MainContract
import uk.themeadow.doineedacache.MainPresenterImpl
import uk.themeadow.doineedacache.repository.SettingsRepository

class MainPresenterTest {

    lateinit var presenter: MainContract.Presenter
    val settingsRepository = mockk<SettingsRepository>()
    val view = mockk<MainContract.View>()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { view.showHelp() } answers {  }

        presenter = MainPresenterImpl(settingsRepository)
        presenter.bind(view)
    }

    @Test
    fun `first run saves and shows help`() {
        every { settingsRepository.get(any()) } returns null
        every { settingsRepository.save(any(), any()) } answers { }

        presenter.checkIfFirstRun()

        verify { view.showHelp() }
        verify { settingsRepository.save(any(), any()) }
    }

    @Test
    fun `subsequent runs dont save or show help`() {
        every { settingsRepository.get(any()) } returns "stuff"
        every { settingsRepository.save(any(), any()) } answers { }

        presenter.checkIfFirstRun()

        verify(exactly = 0) { view.showHelp() }
        verify(exactly = 0) { settingsRepository.save(any(), any()) }
    }
}