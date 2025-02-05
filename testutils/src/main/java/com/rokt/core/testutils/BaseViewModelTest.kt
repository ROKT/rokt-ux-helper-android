package com.rokt.core.testutils

import org.junit.Rule

open class BaseViewModelTest : MockkUnitTest() {

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()
}
