package com.core.testutils.mockserver

import androidx.test.platform.app.InstrumentationRegistry
import com.core.testutils.rule.MockServerConfig
import okhttp3.Headers
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray

class TestServer {

    private val mockServer: MockWebServer = MockWebServer()
    private val dispatcher = MockServerDispatcher()

    fun startServer() {
        mockServer.start()
        mockServer.dispatcher = dispatcher
    }

    fun getUrl() = mockServer.url("/").toUrl()

    fun stopServer() {
        mockServer.shutdown()
    }

    fun setMockServerConfigs(mockServerConfig: MockServerConfig?) {
        if (mockServerConfig != null) {
            dispatcher.mockServerConfig = mockServerConfig
        }
    }

    fun getValidationFailures(): List<String> {
        return dispatcher.failedHeaderValidations.also {
            if (dispatcher.failedBodyValidation.isNotBlank()) {
                it.add(dispatcher.failedBodyValidation)
            }
        }
    }

    fun getCapturedEvents(): List<CapturedEvent> = dispatcher.capturedEvents
}

class MockServerDispatcher : Dispatcher() {

    internal var mockServerConfig: MockServerConfig = MockServerConfig()

    internal val failedHeaderValidations = mutableListOf<String>()
    internal var failedBodyValidation = ""
    internal val capturedEvents = mutableListOf<CapturedEvent>()
    override fun dispatch(request: RecordedRequest): MockResponse {
        captureEvents(request)
        validateRequest(request)
        return getMatchingMockResponse(request.path)
            ?: if (mockServerConfig.defaultResponseSuccess) {
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{}")
            } else {
                MockResponse()
                    .setResponseCode(500)
            }
    }

    private fun getMatchingMockResponse(path: String?): MockResponse? {
        return mockServerConfig.routeConfigs.firstOrNull { it.path == path }?.let {
            val headers = with(Headers.Builder()) {
                it.responseConfig.headers.forEach { headerItem ->
                    add(headerItem.key, headerItem.value)
                }
                build()
            }
            MockResponse()
                .setResponseCode(it.responseConfig.responseCode)
                .setHeaders(headers)
                .setBody(
                    InstrumentationRegistry.getInstrumentation()
                        .context.classLoader.getResource(it.responseConfig.responseResourceFile).readText(),
                )
        }
    }

    private fun validateRequest(request: RecordedRequest) {
        mockServerConfig.routeConfigs.firstOrNull { it.path == request.path }?.let {
            it.requestValidation.headers.forEach { headerItem ->
                val headerValue = request.getHeader(headerItem.key)
                if (headerItem.value != headerValue) {
                    if (!request.headers.names().contains(headerItem.key)) {
                        failedHeaderValidations.add("Header does not contain key ${headerItem.key}")
                    } else {
                        failedHeaderValidations.add(
                            "Header value for \"${headerItem.key}\" mismatches. " +
                                "Expected \"${headerItem.value}\" got \"$headerValue\"",
                        )
                    }
                }
            }

            if (it.requestValidation.body.isNotBlank()) {
                val bodyString = request.body.readUtf8()
                if (bodyString != it.requestValidation.body) {
                    failedBodyValidation = "Expected request body \"${it.requestValidation.body}\", got \"$bodyString\""
                }
            }
        }
    }

    private fun captureEvents(request: RecordedRequest) {
        if (request.path == "/v2/events") {
            val eventsArray = JSONArray(request.body.readUtf8())
            for (i in 0 until eventsArray.length()) {
                val event = eventsArray.getJSONObject(i)
                capturedEvents.add(
                    CapturedEvent(
                        eventType = event.getString("eventType"),
                        sessionId = event.getString("sessionId"),
                        parentGuid = event.getString("parentGuid"),
                        token = event.getString("token"),
                    ),
                )
            }
        }
    }
}
