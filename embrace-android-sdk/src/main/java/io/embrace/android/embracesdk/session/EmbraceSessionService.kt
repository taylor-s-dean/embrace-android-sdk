package io.embrace.android.embracesdk.session

import io.embrace.android.embracesdk.comms.delivery.DeliveryService
import io.embrace.android.embracesdk.internal.clock.Clock
import io.embrace.android.embracesdk.logging.InternalEmbraceLogger
import io.embrace.android.embracesdk.payload.Session
import io.embrace.android.embracesdk.payload.Session.LifeEventType
import io.embrace.android.embracesdk.payload.SessionMessage
import io.embrace.android.embracesdk.session.caching.PeriodicSessionCacher

internal class EmbraceSessionService(
    private val logger: InternalEmbraceLogger,
    private val deliveryService: DeliveryService,
    private val payloadMessageCollator: PayloadMessageCollator,
    private val clock: Clock,
    private val periodicSessionCacher: PeriodicSessionCacher,
    private val orchestrationLock: Any // synchronises session orchestration. Temporarily passed in.
) : SessionService {

    /**
     * The currently active session.
     */
    @Volatile
    override var activeSession: Session? = null

    override fun startSessionWithState(timestamp: Long, coldStart: Boolean): String {
        return startSession(
            InitialEnvelopeParams.SessionParams(
                coldStart,
                LifeEventType.STATE,
                timestamp
            )
        )
    }

    override fun startSessionWithManual(timestamp: Long): String {
        return startSession(
            InitialEnvelopeParams.SessionParams(
                false,
                LifeEventType.MANUAL,
                timestamp
            )
        )
    }

    override fun endSessionWithState(timestamp: Long) {
        endSessionImpl(LifeEventType.STATE, timestamp)
    }

    override fun endSessionWithManual(timestamp: Long) {
        endSessionImpl(LifeEventType.MANUAL, timestamp) ?: return
    }

    override fun endSessionWithCrash(timestamp: Long, crashId: String) {
        val session = activeSession ?: return
        logger.logDebug("SessionHandler: running onCrash for $crashId")
        createAndProcessSessionSnapshot(
            FinalEnvelopeParams.SessionParams(
                initial = session,
                endTime = timestamp,
                lifeEventType = LifeEventType.STATE,
                crashId = crashId,
                endType = SessionSnapshotType.JVM_CRASH
            )
        )
        activeSession = null
    }

    /**
     * It performs all corresponding operations in order to start a session.
     */
    private fun startSession(params: InitialEnvelopeParams.SessionParams): String {
        logger.logDebug(
            "SessionHandler: running onSessionStarted. coldStart=${params.coldStart}," +
                " startType=${params.startType}, startTime=${params.startTime}"
        )

        val session = payloadMessageCollator.buildInitialSession(
            params
        )
        activeSession = session
        logger.logDeveloper(
            "SessionHandler",
            "Started new session. ID=${session.sessionId}"
        )

        periodicSessionCacher.start(::onPeriodicCacheActiveSessionImpl)
        return session.sessionId
    }

    /**
     * It performs all corresponding operations in order to end a session.
     */
    private fun endSessionImpl(
        endType: LifeEventType,
        endTime: Long
    ): SessionMessage? {
        val session = activeSession ?: return null
        logger.logDebug("SessionHandler: running onSessionEnded. endType=$endType, endTime=$endTime")
        val fullEndSessionMessage = createAndProcessSessionSnapshot(
            FinalEnvelopeParams.SessionParams(
                initial = session,
                endTime = endTime,
                lifeEventType = endType,
                endType = SessionSnapshotType.NORMAL_END
            ),
        )
        activeSession = null
        logger.logDebug("SessionHandler: cleared active session")
        return fullEndSessionMessage
    }

    /**
     * Snapshots the active session. The behavior is controlled by the
     * [SessionSnapshotType] passed to this function.
     */
    private fun createAndProcessSessionSnapshot(params: FinalEnvelopeParams.SessionParams): SessionMessage {
        if (params.endType.shouldStopCaching) {
            periodicSessionCacher.stop()
        }

        return payloadMessageCollator.buildFinalSessionMessage(params).also {
            deliveryService.sendSession(it, params.endType)
        }
    }

    /**
     * Called when the session is persisted every 2s to cache its state.
     */
    private fun onPeriodicCacheActiveSessionImpl(): SessionMessage? {
        synchronized(orchestrationLock) {
            val session = activeSession ?: return null
            return createAndProcessSessionSnapshot(
                FinalEnvelopeParams.SessionParams(
                    initial = session,
                    endTime = clock.now(),
                    lifeEventType = LifeEventType.STATE,
                    endType = SessionSnapshotType.PERIODIC_CACHE
                ),
            )
        }
    }
}
