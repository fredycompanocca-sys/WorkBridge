package com.tecsup.workbridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tecsup.workbridge.ui.navigation.AppNavigation
import com.tecsup.workbridge.ui.theme.WorkBridgeTheme

/**
 * Actividad Principal (Single Activity Architecture).
 * Todo el flujo de pantallas y vistas es gestionado reactivamente por Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as WorkBridgeApp

        setContent {
            WorkBridgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authRepository = app.authRepository,
                        trabajoRepository = app.trabajoRepository
                    )
                }
            }
        }
    }
}
