package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector

private val PlayVolumeValues: List<Float> =
    listOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
private val PlaySpeedValues: List<Float> =
    listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)

private val PlayPitchValues: List<Float> = listOf(
    0.5f,
    0.52973f,
    0.56123f,
    0.59460f,
    0.62996f,
    0.66742f,
    0.70711f,
    0.74915f,
    0.79370f,
    0.84090f,
    0.89090f,
    0.94387f,
    1.0f,
    1.05946f,
    1.12246f,
    1.18921f,
    1.25992f,
    1.33484f,
    1.41421f,
    1.49831f,
    1.58740f,
    1.68179f,
    1.78180f,
    1.88775f,
    2.0f
)

@Composable
fun AudioPlayView(item: PlayAudioDto) {


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VolumeDropdownSelector()
            SpeedDropdownSelector()
            PitchDropdownSelector()

            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = stringResource(R.string.repeat_description),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = stringResource(R.string.list_description)
                )
            }
        }
    }
}

@Composable
private fun VolumeDropdownSelector(volume: Float = 1.0f) {
    val title = stringResource(R.string.volume_title)
    val volumeLabels = stringArrayResource(R.array.play_volume_labels).toList()

    val index = PlayVolumeValues.indexOf(volume)
    val selectedLabel =
        if (index < 0) stringResource(
            R.string.not_found_speed_label,
            volume
        ) else volumeLabels[index]

    TextDropdownSelector(volumeLabels, selectedLabel, title = title, onItemSelected = {})
}

@Composable
private fun SpeedDropdownSelector(speed: Float = 1.0f) {
    val title = stringResource(R.string.speed_title)
    val speedLabels = stringArrayResource(R.array.play_speed_labels).toList()

    val index = PlaySpeedValues.indexOf(speed)
    val selectedLabel =
        if (index < 0) stringResource(R.string.not_found_speed_label, speed) else speedLabels[index]

    TextDropdownSelector(speedLabels, selectedLabel, title = title, onItemSelected = {})
}

@Composable
private fun PitchDropdownSelector(speed: Float = 1.0f) {
    val title = stringResource(R.string.pitch_title)
    val pitchLabels = stringArrayResource(R.array.play_pitch_labels).toList()

    val index = PlayPitchValues.indexOf(speed)
    val selectedLabel =
        if (index < 0) stringResource(R.string.not_found_speed_label, speed) else pitchLabels[index]

    TextDropdownSelector(pitchLabels, selectedLabel, title = title, onItemSelected = {})
}
