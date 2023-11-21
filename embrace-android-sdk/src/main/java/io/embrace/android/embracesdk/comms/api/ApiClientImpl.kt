package io.embrace.android.embracesdk.comms.api

import io.embrace.android.embracesdk.comms.api.ApiClient.Companion.NO_HTTP_RESPONSE
import io.embrace.android.embracesdk.comms.api.ApiClient.Companion.TOO_MANY_REQUESTS
import io.embrace.android.embracesdk.comms.api.ApiClient.Companion.defaultTimeoutMs
import io.embrace.android.embracesdk.logging.InternalEmbraceLogger
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_ENTITY_TOO_LARGE
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_OK
import java.util.zip.GZIPOutputStream

/**
 * Client for calling the Embrace API. This service handles all calls to the Embrace API.
 *
 * Sessions can be sent to either the production or development endpoint. The development
 * endpoint shows sessions on the 'integration testing' screen within the dashboard, whereas
 * the production endpoint sends sessions to 'recent sessions'.
 *
 * The development endpoint is only used if the build is a debug build, and if integration
 * testing is enabled when calling [Embrace.start()].
 */
internal class ApiClientImpl(
    private val logger: InternalEmbraceLogger
) : ApiClient {

    override fun executeGet(request: ApiRequest): ApiResponse {
        var connection: EmbraceConnection? = null

        return try {
            connection = request.toConnection()
            setTimeouts(connection)
            connection.connect()
            val response = executeHttpRequest(connection)
            response
        } catch (ex: Throwable) {
            ApiResponse.Incomplete(IllegalStateException(ex.localizedMessage ?: "", ex))
        } finally {
            runCatching {
                connection?.inputStream?.close()
            }
        }
    }

    override fun executePost(request: ApiRequest, payloadToCompress: ByteArray): ApiResponse =
        executeRawPost(request, gzip(payloadToCompress))

    /**
     * Posts a payload according to the ApiRequest parameter. The payload will not be gzip compressed.
     */
    private fun executeRawPost(request: ApiRequest, payload: ByteArray?): ApiResponse {
        logger.logDeveloper("ApiClient", request.httpMethod.toString() + " " + request.url)
        logger.logDeveloper("ApiClient", "Request details: $request")

        var connection: EmbraceConnection? = null
        return try {
            connection = request.toConnection()
            setTimeouts(connection)
            if (payload != null) {
                logger.logDeveloper("ApiClient", "Payload size: " + payload.size)
                connection.outputStream?.write(payload)
                connection.connect()
            }
            val response = executeHttpRequest(connection)
            response
        } catch (ex: Throwable) {
            ApiResponse.Incomplete(IllegalStateException(ex.localizedMessage ?: "", ex))
        } finally {
            runCatching {
                connection?.inputStream?.close()
            }
        }
    }

    private fun setTimeouts(connection: EmbraceConnection) {
        connection.setConnectTimeout(defaultTimeoutMs)
        connection.setReadTimeout(defaultTimeoutMs)
    }

    /**
     * Executes a HTTP call using the specified connection, returning the response from the
     * server as a string.
     */
    private fun executeHttpRequest(connection: EmbraceConnection): ApiResponse {
        return try {
            val responseCode = readHttpResponseCode(connection)
            val responseHeaders = readHttpResponseHeaders(connection)

            return when (responseCode) {
                HTTP_OK -> {
                    val responseBody = readResponseBodyAsString(connection.inputStream)
                    ApiResponse.Success(responseBody, responseHeaders)
                }
                HTTP_NOT_MODIFIED -> {
                    ApiResponse.NotModified
                }
                HTTP_ENTITY_TOO_LARGE -> {
                    ApiResponse.PayloadTooLarge
                }
                TOO_MANY_REQUESTS -> {
                    val retryAfter = responseHeaders["Retry-After"]?.toLongOrNull()
                    ApiResponse.TooManyRequests(retryAfter)
                }
                NO_HTTP_RESPONSE -> {
                    ApiResponse.Incomplete(IllegalStateException("Connection failed or unexpected response code"))
                }
                else -> {
                    ApiResponse.Failure(responseCode, responseHeaders)
                }
            }
        } catch (exc: Throwable) {
            ApiResponse.Incomplete(IllegalStateException("Error occurred during HTTP request execution", exc))
        }
    }

    private fun readHttpResponseCode(connection: EmbraceConnection): Int {
        var responseCode: Int? = null
        try {
            responseCode = connection.responseCode
            logger.logDeveloper("ApiClient", "Response status: $responseCode")
        } catch (ex: IOException) {
            logger.logDeveloper("ApiClient", "Connection failed or unexpected response code")
        }
        return responseCode ?: NO_HTTP_RESPONSE
    }

    private fun readHttpResponseHeaders(connection: EmbraceConnection): Map<String, String> {
        val headers = connection.headerFields?.mapValues { it.value.joinToString() } ?: emptyMap()

        headers.forEach { entry ->
            logger.logDeveloper("ApiClient", "Response header: ${entry.key}: ${entry.value}")
        }
        return headers
    }

    /**
     * Reads an [InputStream] into a String.
     *
     * @param inputStream the input stream to read
     * @return the string representation
     */
    private fun readResponseBodyAsString(inputStream: InputStream?): String {
        return try {
            val body = InputStreamReader(inputStream).buffered().use {
                it.readText()
            }
            logger.logDeveloper("ApiClient", "Successfully read response body.")
            body
        } catch (ex: IOException) {
            logger.logDeveloper("ApiClient", "Failed to read response body.", ex)
            throw IllegalStateException("Failed to read response body.", ex)
        }
    }

    /**
     * Compresses a given byte array using the GZIP compression algorithm.
     *
     * @param bytes the byte array to compress
     * @return the compressed byte array
     */
    private fun gzip(bytes: ByteArray): ByteArray {
        return try {
            ByteArrayOutputStream().use { baos ->
                GZIPOutputStream(baos).use { gzipStream ->
                    gzipStream.write(bytes)
                    gzipStream.finish()
                }
                baos.toByteArray()
            }
        } catch (ex: IOException) {
            throw IllegalStateException("Failed to gzip payload.", ex)
        }
    }
}