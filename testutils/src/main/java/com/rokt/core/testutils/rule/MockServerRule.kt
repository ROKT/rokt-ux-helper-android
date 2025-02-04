package com.rokt.core.testutils.rule

import com.rokt.core.testutils.mockserver.TestServer
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServerRule : ExternalResource() {

    private val testServer = TestServer()

    fun getBaseUrl() = testServer.getUrl().toString()

    fun getCapturedEvents() = testServer.getCapturedEvents()

    override fun before() {
        testServer.startServer()
    }

    override fun after() {
        testServer.stopServer()
        val validationErrors = testServer.getValidationFailures()
        if (validationErrors.isNotEmpty()) {
            throw Exception(validationErrors.joinToString("\n"))
        }
    }

    override fun apply(base: Statement?, description: Description?): Statement {
        testServer.setMockServerConfigs(description?.getAnnotation(MockServerConfig::class.java))
        return super.apply(base, description)
    }
}
