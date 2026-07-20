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
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogResponseComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CatalogResponseButtonComponent/CatalogResponseButton_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_partner_managed_catalog_item.json")
    fun testPartnerManagedPurchaseEmitsInstantPurchaseEvent() {
        composeTestRule.onNodeWithText("Confirm order").assertIsDisplayed()

        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).performClick()

        val event = waitForInstantPurchaseEvent()

        assertThat(event.catalogItemModel.get<String>("catalogItemId")).isEqualTo("catalog-item-1")
        assertThat(getCapturedEvents().filterIsInstance<LayoutContract.LayoutEvent.CartItemForwardPaymentSelected>())
            .isEmpty()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogResponseButtonComponent/CatalogResponseButton_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_forward_payment_catalog_item.json")
    fun testNonPartnerManagedPurchaseEmitsForwardPaymentEvent() {
        composeTestRule.onNodeWithText("Confirm order").assertIsDisplayed()

        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).performClick()

        val event = waitForForwardPaymentEvent()

        assertThat(event.offerId).isEqualTo(0)
        assertThat(event.catalogItemModel.get<String>("catalogItemId")).isEqualTo("catalog-item-1")
        assertThat(event.transactionData?.isPartnerManagedPurchase).isFalse()
        assertThat(event.transactionData?.partnerPaymentReference).isEqualTo("partner-reference")
        assertThat(getCapturedEvents().filterIsInstance<LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected>())
            .isEmpty()
    }

    private fun waitForInstantPurchaseEvent(): LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            getCapturedEvents().any { event ->
                event is LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected
            }
        }

        return getCapturedEvents()
            .filterIsInstance<LayoutContract.LayoutEvent.CartItemInstantPurchaseSelected>()
            .first()
    }

    private fun waitForForwardPaymentEvent(): LayoutContract.LayoutEvent.CartItemForwardPaymentSelected {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            getCapturedEvents().any { event ->
                event is LayoutContract.LayoutEvent.CartItemForwardPaymentSelected
            }
        }

        return getCapturedEvents()
            .filterIsInstance<LayoutContract.LayoutEvent.CartItemForwardPaymentSelected>()
            .first()
    }
}
