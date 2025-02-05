package com.rokt.roktux.viewmodel

import com.rokt.core.testutils.MockkUnitTest
import com.rokt.core.testutils.TestCoroutineRule
import org.junit.Rule

open class BaseViewModelTest : MockkUnitTest() {

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()
}
