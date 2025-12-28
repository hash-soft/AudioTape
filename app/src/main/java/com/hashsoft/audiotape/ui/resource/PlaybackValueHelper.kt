package com.hashsoft.audiotape.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues


@Composable
fun displayVolumeValue(volume: Float): Triple<Int, String, List<String>> {
    val labels = stringArrayResource(R.array.play_volume_labels).toList()

    val index = PlayVolumeValues.indexOf(volume)
    val label = if (index < 0) stringResource(
        R.string.not_found_volume_label,
        (volume * 100).toInt()
    ) else labels.getOrElse(index) { "" }

    return Triple(index, label, labels)
}

@Composable
fun displaySpeedValue(speed: Float): Triple<Int, String, List<String>> {
    val labels = stringArrayResource(R.array.play_speed_labels).toList()

    val index = PlaySpeedValues.indexOf(speed)
    val label = if (index < 0) stringResource(
        R.string.not_found_speed_label,
        speed
    ) else labels.getOrElse(index) { "" }

    return Triple(index, label, labels)
}

@Composable
fun displayPitchValue(pitch: Float): Triple<Int, String, List<String>> {
    val labels = stringArrayResource(R.array.play_pitch_labels).toList()

    val index = PlayPitchValues.indexOf(pitch)
    val label = if (index < 0) stringResource(
        R.string.not_found_pitch_label,
        pitchToSemitone(pitch)
    ) else labels.getOrElse(index) { "" }

    return Triple(index, label, labels)
}

private fun pitchToSemitone(pitch: Float): Float {
    return (12 * kotlin.math.log2(pitch.toDouble())).toFloat()
}