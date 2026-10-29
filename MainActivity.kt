package com.muzafrik.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzafrik.app.ui.MuZafrikRoot
import com.muzafrik.app.ui.permissions.PermissionGate
import com.muzafrik.app.ui.settings.SettingsViewModel
import com.muzafrik.app.ui.theme.MuZafrikTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings: SettingsViewModel = hiltViewModel()
            val themeMode by settings.themeMode.collectAsStateWithLifecycle()
            MuZafrikTheme(themeMode) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PermissionGate { MuZafrikRoot(settings) }
                }
            }
        }
    }
}
