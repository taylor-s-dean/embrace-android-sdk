package io.embrace.android.embracesdk.internal.spans

import io.embrace.android.embracesdk.internal.spans.EmbraceSpanData.Companion.fromEventData
import io.opentelemetry.sdk.trace.data.SpanData

internal fun SpanData.toEmbraceSpanData() = EmbraceSpanData(
    traceId = spanContext.traceId,
    spanId = spanContext.spanId,
    parentSpanId = parentSpanId,
    name = name,
    startTimeNanos = startEpochNanos,
    endTimeNanos = endEpochNanos,
    status = status.statusCode,
    events = fromEventData(eventDataList = events),
    attributes = attributes.toStringMap(),
)
