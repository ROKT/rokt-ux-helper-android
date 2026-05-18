package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.modelmapper.hmap.get
import com.rokt.network.model.PaymentProvider
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogDevicePayButtonComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDevicePayButtonComponent/CatalogDevicePayButton_google_pay.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testGooglePayRendersChildrenAndEmitsDevicePayEvent() {
        composeTestRule.onNodeWithText("Buy with Google Pay").assertIsDisplayed()

        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).performClick()

        val event = waitForDevicePayEvent(PaymentProvider.GooglePay)

        assertThat(event.validatorFieldKeys).containsExactly("dropDownSelection")
        assertThat(event.catalogItemModel?.get<String>("catalogItemId")).isEqualTo("catalog-item-1")
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDevicePayButtonComponent/CatalogDevicePayButton_card.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testCardRendersChildrenAndEmitsCardProvider() {
        assertChildButtonEmitsProvider("Pay with card", PaymentProvider.Card)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDevicePayButtonComponent/CatalogDevicePayButton_stripe.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testStripeRendersChildrenAndEmitsStripeProvider() {
        assertChildButtonEmitsProvider("Pay with Stripe", PaymentProvider.Stripe)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDevicePayButtonComponent/CatalogDevicePayButton_afterpay.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testAfterpayRendersChildrenAndEmitsAfterpayProvider() {
        assertChildButtonEmitsProvider("Pay with Afterpay", PaymentProvider.Afterpay)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDevicePayButtonComponent/CatalogDevicePayButton_paypal.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testPaypalRendersChildrenAndEmitsPaypalProvider() {
        assertChildButtonEmitsProvider("Pay with Paypal", PaymentProvider.Paypal)
    }

    private fun assertChildButtonEmitsProvider(label: String, provider: PaymentProvider) {
        composeTestRule.onNodeWithText(label).assertIsDisplayed()

        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).performClick()

        val event = waitForDevicePayEvent(provider)

        assertThat(event.catalogItemModel?.get<String>("catalogItemId")).isEqualTo("catalog-item-1")
    }

    private fun waitForDevicePayEvent(provider: PaymentProvider): LayoutContract.LayoutEvent.CartItemDevicePaySelected {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            getCapturedEvents().any { event ->
                (event as? LayoutContract.LayoutEvent.CartItemDevicePaySelected)?.paymentProvider == provider
            }
        }

        return getCapturedEvents()
            .filterIsInstance<LayoutContract.LayoutEvent.CartItemDevicePaySelected>()
            .first { event -> event.paymentProvider == provider }
    }
}
