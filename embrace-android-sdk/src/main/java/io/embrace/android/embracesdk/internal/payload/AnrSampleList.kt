package io.embrace.android.embracesdk.internal.payload

/**
 * Holds a list of [AnrSample] objects.
 */
internal data class AnrSampleList(

    /**
     * List of samples.
     */
    val samples: List<AnrSample>
)