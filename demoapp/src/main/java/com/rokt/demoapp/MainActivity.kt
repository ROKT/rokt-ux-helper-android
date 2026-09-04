package com.rokt.demoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rokt.demoapp.util.DemoTheme
import kotlinx.coroutines.launch

/** Sample app entry point, mirroring `ExampleApp` on iOS. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val failureMessage = stringResource(R.string.layout_failed_to_load)
                // Saveable so an in-progress experience survives rotation; RoktLayout keeps its
                // own state across configuration change, and losing the host's would discard it.
                var selected by rememberSaveable { mutableStateOf<SampleDestination?>(null) }

                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
                    when (val destination = selected) {
                        null -> HomeScreen(
                            onLaunch = { launched ->
                                if (launched == SampleDestination.ViewSystem) {
                                    startActivity(Intent(this, SampleLayoutViewActivity::class.java))
                                } else {
                                    selected = launched
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                        )

                        else -> {
                            BackHandler { selected = null }
                            // No Scaffold insets here: overlay and bottom-sheet layouts paint
                            // their own full-bleed backdrop, which insetting would leave striped.
                            SampleScreen(
                                experienceAsset = destination.asset,
                                location = destination.location,
                                modifier = Modifier.fillMaxSize(),
                                onFinished = { selected = null },
                                onFailure = {
                                    selected = null
                                    scope.launch { snackbarHostState.showSnackbar(failureMessage) }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
