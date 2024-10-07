package io.embrace.android.embracesdk.internal.logging

/**
 * Represents a type of internal error that can be recorded for the Embrace SDK's telemetry.
 */
enum class InternalErrorType {
    UNCAUGHT_EXC_HANDLER,
    ANR_DATA_FETCH,
    DISABLE_DATA_CAPTURE,
    ENABLE_DATA_CAPTURE,
    NETWORK_STATUS_CAPTURE_FAIL,
    SCREEN_RES_CAPTURE_FAIL,
    CONFIG_LISTENER_FAIL,
    MEMORY_CLEAN_LISTENER_FAIL,
    BG_SESSION_CACHE_FAIL,
    FG_SESSION_CACHE_FAIL,
    ACTIVITY_LISTENER_FAIL,
    PROCESS_STATE_CALLBACK_FAIL,
    TIME_TRAVEL,
    ANR_HEARTBEAT_CHECK_FAIL,
    CFG_CHANGE_DATA_CAPTURE_FAIL,
    SESSION_CHANGE_DATA_CAPTURE_FAIL,
    DATA_SOURCE_DATA_CAPTURE_FAIL,
    DISK_STAT_CAPTURE_FAIL,
    USER_LOAD_FAIL,
    WEB_VITAL_PARSE_FAIL,
    START_EVENT_FAIL,
    END_EVENT_FAIL,
    NATIVE_THREAD_SAMPLE_FAIL,
    NATIVE_CRASH_LOAD_FAIL,
    INVALID_NATIVE_SYMBOLS,
    NATIVE_HANDLER_INSTALL_FAIL,
    SAFE_DATA_CAPTURE_FAIL,
    PROCESS_STATE_SUMMARY_FAIL,
    ANR_HEARTBEAT_STOP_FAIL,
    SDK_START_FAIL,
    UNKNOWN_DELIVERY_ERROR,
    PAYLOAD_RESURRECTION_FAIL,
}
