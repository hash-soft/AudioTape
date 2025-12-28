package com.hashsoft.audiotape.ui.resource

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Method

class PlaybackValueHelperUnitTest {

    @Test
    fun testPitchToSemitone() {
        val klass = Class.forName("com.hashsoft.audiotape.ui.resource.PlaybackValueHelperKt")
        val pitchToSemitoneMethod: Method = klass.getDeclaredMethod("pitchToSemitone", java.lang.Float.TYPE)
        pitchToSemitoneMethod.isAccessible = true

        // pitch = 1.0f -> 0 semitone
        var result = pitchToSemitoneMethod.invoke(null, 1.0f) as Float
        assertEquals(0.0f, result, 0.001f)

        // pitch = 2.0f -> 12 semitone
        result = pitchToSemitoneMethod.invoke(null, 2.0f) as Float
        assertEquals(12.0f, result, 0.001f)

        // pitch = 0.5f -> -12 semitone
        result = pitchToSemitoneMethod.invoke(null, 0.5f) as Float
        assertEquals(-12.0f, result, 0.001f)
    }
}
