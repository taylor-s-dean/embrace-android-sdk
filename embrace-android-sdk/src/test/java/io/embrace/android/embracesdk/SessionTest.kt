package io.embrace.android.embracesdk

import io.embrace.android.embracesdk.payload.Session
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

internal class SessionTest {

    private val info = Session(
        endTime = 987654321L,
        lastHeartbeatTime = 123456780L,
        terminationTime = 16090292309L
    )

    @Test
    fun testSerialization() {
        assertJsonMatchesGoldenFile("session_expected.json", info)
    }

    @Test
    fun testDeserialization() {
        val obj = deserializeJsonFromResource<Session>("session_expected.json")
        assertNotNull(obj)

        with(obj) {
            assertEquals(987654321L, endTime)
            assertEquals(16090292309L, terminationTime)
            assertEquals(123456780L, lastHeartbeatTime)
        }
    }
}
