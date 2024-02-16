package io.embrace.android.embracesdk.internal.logs

import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.logs.data.LogRecordData

internal class LogSinkImpl(private val logOrchestrator: LogOrchestrator) : LogSink {
    private val storedLogs: MutableList<EmbraceLogRecordData> = mutableListOf()

    override fun storeLogs(logs: List<LogRecordData>): CompletableResultCode {
        try {
            synchronized(storedLogs) {
                storedLogs += logs.map { EmbraceLogRecordData(logRecordData = it) }
            }
            logOrchestrator.onLogsAdded()
        } catch (t: Throwable) {
            return CompletableResultCode.ofFailure()
        }

        return CompletableResultCode.ofSuccess()
    }

    override fun completedLogs(): List<EmbraceLogRecordData> {
        synchronized(storedLogs) {
            return storedLogs.toList()
        }
    }

    override fun flushLogs(): List<EmbraceLogRecordData> {
        synchronized(storedLogs) {
            val flushedLogs = storedLogs.toList()
            storedLogs.clear()
            return flushedLogs
        }
    }
}
