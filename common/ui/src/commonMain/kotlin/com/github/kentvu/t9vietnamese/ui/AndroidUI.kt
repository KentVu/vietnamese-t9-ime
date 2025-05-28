package com.github.kentvu.t9vietnamese.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class AndroidUI(
    private val desktopUI: DesktopUI,
    private val launchSystemImSettings: () -> Unit,
    private val launchImePicker: () -> Unit,
) : CommonUI by desktopUI {

    @Composable
    fun AppUi() = with(desktopUI) {
        val state by stateSource.collectAsState()
        AppUiWrapper(state) {
            GuideUserUI(/*Modifier.weight(1f)*/)
            SelectModeUI(state)
            TestTextField(Modifier.weight(1f))
            CandidateView(state,)
            ImeUI(state, Modifier)
        }
    }

    @Composable
    private fun /*ColumnScope.*/GuideUserUI(
        modifier: Modifier = Modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth(),
        ) {
            Text("Please enable T9Vietnamese:", Modifier.align(Alignment.Start))
            Button(
                { launchSystemImSettings() },
                Modifier.align(Alignment.Start)
            ) {
                Text("open system im settings")
            }
            Text("You can choose T9Vietnamese as default IM:", Modifier.align(Alignment.Start))
            Button(
                { launchImePicker() },
                Modifier.align(Alignment.Start)
            ) {
                Text("Launch IME picker")
            }
        }
    }
}