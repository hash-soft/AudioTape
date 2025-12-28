package com.hashsoft.audiotape.ui.resource

import androidx.compose.ui.test.junit4.createComposeRule
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PlaybackValueHelperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDisplayVolumeValue() {
        composeTestRule.setContent {
            val volume = 0.5f
            val (index, label, _) = displayVolumeValue(volume)
            assertEquals(PlayVolumeValues.indexOf(volume), index)
            assertEquals("50%", label)
        }
    }

    @Test
    fun testDisplaySpeedValue() {
        composeTestRule.setContent {
            val speed = 1.5f
            val (index, label, _) = displaySpeedValue(speed)
            assertEquals(PlaySpeedValues.indexOf(speed), index)
            assertEquals("x1.5", label)
        }
    }

    @Test
    fun testDisplayPitchValue() {
        composeTestRule.setContent {
            val pitch = 1.0f
            val (index, label, _) = displayPitchValue(pitch)
            assertEquals(PlayPitchValues.indexOf(pitch), index)
            assertEquals("±0", label)
        }
    }
}
