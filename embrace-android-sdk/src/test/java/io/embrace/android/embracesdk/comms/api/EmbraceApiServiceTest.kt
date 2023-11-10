package io.embrace.android.embracesdk.comms.api

import io.embrace.android.embracesdk.ResourceReader
import io.embrace.android.embracesdk.capture.connectivity.NetworkConnectivityService
import io.embrace.android.embracesdk.comms.delivery.DeliveryCacheManager
import io.embrace.android.embracesdk.comms.delivery.NetworkStatus
import io.embrace.android.embracesdk.concurrency.BlockingScheduledExecutorService
import io.embrace.android.embracesdk.config.remote.RemoteConfig
import io.embrace.android.embracesdk.internal.EmbraceSerializer
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.ScheduledExecutorService

internal class EmbraceApiServiceTest {

    companion object {
        private lateinit var mockApiUrlBuilder: ApiUrlBuilder
        private lateinit var mockApiClient: ApiClient
        private lateinit var mockCacheManager: DeliveryCacheManager
        private lateinit var blockingScheduledExecutorService: BlockingScheduledExecutorService
        private lateinit var testScheduledExecutor: ScheduledExecutorService
        private lateinit var networkConnectivityService: NetworkConnectivityService
        private lateinit var cachedConfig: CachedConfig
        private lateinit var apiService: EmbraceApiService

        @BeforeClass
        @JvmStatic
        fun setupBeforeAll() {
            mockApiUrlBuilder = mockk(relaxUnitFun = true) {
                every { getEmbraceUrlWithSuffix(any()) } returns "http://fake.url"
                every { getConfigUrl() } returns "https://config.url"
            }
            networkConnectivityService = mockk(relaxUnitFun = true)
            blockingScheduledExecutorService = BlockingScheduledExecutorService()
            testScheduledExecutor = blockingScheduledExecutorService
        }

        /**
         * Setup after all tests get executed. Un-mock all here.
         */
        @AfterClass
        @JvmStatic
        fun tearDownAfterAll() {
            unmockkAll()
        }
    }

    @Before
    fun setUp() {
        mockApiClient = mockk {
            every { executePost(any(), any()) } returns ""
        }
        cachedConfig = CachedConfig(
            config = null,
            eTag = null
        )
        mockCacheManager = mockk(relaxUnitFun = true)
    }

    @After
    fun tearDown() {
        clearMocks(mockApiClient)
        clearMocks(mockCacheManager)
    }

    @Test
    fun `test getConfig returns correct values in Response`() {
        val json = ResourceReader.readResourceAsText("remote_config_response.json")
        every { mockApiClient.executeGet(any()) } returns ApiResponse(
            statusCode = 200,
            body = json,
            headers = emptyMap()
        )
        initApiService(
            status = NetworkStatus.NOT_REACHABLE
        )
        val remoteConfig = apiService.getConfig()

        // verify a few fields were serialized correctly.
        checkNotNull(remoteConfig)
        assertTrue(checkNotNull(remoteConfig.sessionConfig?.isEnabled))
        assertFalse(checkNotNull(remoteConfig.sessionConfig?.endAsync))
        assertEquals(100, remoteConfig.threshold)
    }

    @Test(expected = IllegalStateException::class)
    fun `test getConfig rethrows an exception thrown by apiClient`() {
        every { mockApiClient.executeGet(any()) } throws IllegalStateException("Test exception message")
        initApiService(
            status = NetworkStatus.NOT_REACHABLE
        )
        // exception will be thrown and caught by this test's annotation
        apiService.getConfig()
    }

    @Test
    fun testGetConfigWithMatchingEtag() {
        val cfg = RemoteConfig()
        cachedConfig = CachedConfig(cfg, "my_etag")
        every { mockApiClient.executeGet(any()) } returns ApiResponse(
            statusCode = 304,
            body = "",
            headers = emptyMap()
        )
        initApiService()
        val remoteConfig = apiService.getConfig()
        assertSame(cfg, remoteConfig)
    }

    private fun initApiService(status: NetworkStatus = NetworkStatus.NOT_REACHABLE) {
        every { networkConnectivityService.getCurrentNetworkStatus() } returns status

        apiService = EmbraceApiService(
            apiClient = mockApiClient,
            urlBuilder = mockApiUrlBuilder,
            serializer = EmbraceSerializer(),
            cachedConfigProvider = { _, _ -> cachedConfig },
            logger = mockk(relaxed = true),
            scheduledExecutorService = testScheduledExecutor,
            networkConnectivityService = networkConnectivityService,
            cacheManager = mockCacheManager,
            lazyDeviceId = lazy { "07D85B44E4E245F4A30E559BFC0D07FF" },
            appId = "o0o0o",
            deliveryRetryManager = mockk(relaxed = true)
        )
    }
}
