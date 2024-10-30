package com.rokt.roktux.viewmodel

import com.core.testutils.MockkUnitTest
import com.core.testutils.TestCoroutineRule
import org.junit.Rule

open class BaseViewModelTest : MockkUnitTest() {

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()
}
