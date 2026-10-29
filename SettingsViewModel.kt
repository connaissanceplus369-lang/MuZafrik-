package com.muzafrik.app.ui.settings

import androidx.lifecycle.ViewModel
import com.muzafrik.app.data.settings.SettingsRepository
import com.muzafrik.app.data.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = repo.themeMode
    fun setThemeMode(mode: ThemeMode) = repo.setThemeMode(mode)
}
