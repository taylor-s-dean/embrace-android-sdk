package io.embrace.android.embracesdk.internal.logs

import io.embrace.android.embracesdk.fakes.FakeLogRecordData
import io.mockk.mockk
import io.mockk.verify
import io.opentelemetry.sdk.common.CompletableResultCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

internal class LogSinkImplTest {
    private lateinit var logSink: LogSink
    private val logOrchestrator: LogOrchestrator = mockk(relaxed = true)

    @Before
    fun setup() {
        logSink = LogSinkImpl(logOrchestrator)
    }

    @Test
    fun `verify default state`() {
        assertEquals(0, logSink.completedLogs().size)
        assertEquals(0, logSink.flushLogs().size)
        assertEquals(CompletableResultCode.ofSuccess(), logSink.storeLogs(listOf()))
    }

    @Test
    fun `storing logs adds to stored logs`() {
        val resultCode = logSink.storeLogs(listOf(FakeLogRecordData()))
        verify { logOrchestrator.onLogsAdded() }
        assertEquals(CompletableResultCode.ofSuccess(), resultCode)
        assertEquals(1, logSink.completedLogs().size)
        assertEquals(EmbraceLogRecordData(logRecordData = FakeLogRecordData()), logSink.completedLogs().first())
    }

    @Test
    fun `flushing clears stored logs`() {
        logSink.storeLogs(listOf(mockk(relaxed = true), mockk(relaxed = true)))
        val snapshot = logSink.completedLogs()
        assertEquals(2, snapshot.size)

        val flushedLogs = logSink.flushLogs()
        assertEquals(2, flushedLogs.size)
        repeat(2) {
            assertSame(snapshot[it], flushedLogs[it])
        }
        assertEquals(0, logSink.completedLogs().size)
    }
}
