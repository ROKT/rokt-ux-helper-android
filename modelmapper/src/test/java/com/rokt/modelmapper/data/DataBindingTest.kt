package com.rokt.modelmapper.data

import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.testutils.MockkUnitTest
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeLink
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class DataBindingImplTest : MockkUnitTest() {

    private val dataBinding: DataBinding = DataBindingImpl()

    private val offer: OfferModel = mockk(relaxed = true) {
        every { creative } returns mockk(relaxed = true) {
            every { responseOptions } returns
                persistentMapOf(
                    Pair(
                        "positive",
                        mockk(relaxed = true) {
                            every { properties } returns mockk(relaxed = true) {
                                every { get(key = any<TypedKey<Any>>()) } returns null
                                every { get(TypedKey<String>("instanceGuid")) } returns "positiveGuid0"
                                every { get(TypedKey<String>("shortLabel")) } returns "Thank You"
                            }
                        },
                    ),
                    Pair(
                        "negative",
                        mockk(relaxed = false) {
                            every { properties } returns mockk(relaxed = true) {
                                every { get(key = any<TypedKey<Any>>()) } returns null
                                every { get(TypedKey<String>("instanceGuid")) } returns "negativeGuid0"
                                every { get(TypedKey<String>("shortLabel")) } returns "No thanks"
                            }
                        },
                    ),
                )
            every { copy } returns persistentMapOf(
                Pair("disclaimer", "test disclaimer"),
                Pair("title", "test title"),
            )
            every { links } returns persistentMapOf(
                "termsCondition" to CreativeLink("https://rokt.com", "Terms"),
            )
        }
        every { catalogItems } returns listOf<CatalogItemModel>(
            mockk(relaxed = true) {
                every { properties } returns mockk(relaxed = false) {
                    every { get(key = any<TypedKey<Any>>()) } returns null
                    every { get(TypedKey<String>("positiveResponseText1")) } returns "Add to order 1"
                    every { get(TypedKey<String>("negativeResponseText1")) } returns "Dismiss 1"
                }

                every { imageWrapper } returns mockk(relaxed = false) {
                    every { properties } returns mockk(relaxed = false) {
                        every { get(key = any<TypedKey<Any>>()) } returns null
                        every { get(TypedKey<OfferImageModel>("catalogItemImage1")) } returns OfferImageModel(
                            light = "light1",
                            dark = "dark1",
                            alt = "alt1",
                            title = "title1",
                        )
                    }
                }
            },
            mockk(relaxed = true) {
                every { properties } returns mockk(relaxed = false) {
                    every { get(key = any<TypedKey<Any>>()) } returns null
                    every { get(TypedKey<String>("positiveResponseText2")) } returns "Add to order 2"
                    every { get(TypedKey<String>("negativeResponseText2")) } returns "Dismiss 2"
                }

                every { imageWrapper } returns mockk(relaxed = false) {
                    every { properties } returns mockk(relaxed = false) {
                        every { get(key = any<TypedKey<Any>>()) } returns null
                        every { get(TypedKey<OfferImageModel>("catalogItemImage2")) } returns OfferImageModel(
                            light = "light2",
                            dark = "dark2",
                            alt = "alt2",
                            title = "title2",
                        )
                    }
                }
            },
        ).toImmutableList()
    }

    @Test
    fun `when bind data is called with no offer, then default value should return`() {
        // Act
        val value: BindData =
            dataBinding.bindValue(
                "%^DATA.creativeCopy.title|DATA.creativeCopy.copy|defaultValue^%",
                offerModel = null,
            )

        // Assert
        assertTrue(value is BindData.Value && value.text.equals("defaultValue"))
    }

    @Test
    fun `when bind data is called with no offer and default value, then undefined bindValue is returned`() {
        // Act
        val value = dataBinding.bindValue("%^DATA.xxxx.title|DATA.xxxx.copy^%", offerModel = null)

        // Assert
        assertEquals(value, BindData.Undefined)
    }

    @Test
    fun `when bind data is called with creativeResponse namespace and valid responseKey then correct value should be retrieved`() {
        // Act
        val value: BindData =
            dataBinding.bindValue("%^DATA.creativeResponse.shortLabel|defaultValue^%", "positive", offer)

        // Assert
        assertTrue(value is BindData.Value && value.text.equals("Thank You"))
    }

    @Test
    fun `when bind data is called with creativeResponse namespace and invalid responseKey then undefined bindValue is returned`() {
        // Act
        val value = dataBinding.bindValue("%^DATA.creativeResponse.shortLabel^%", "neutral", offer)

        // Assert
        assertEquals(value, BindData.Undefined)
    }

    @Test
    fun `when bind data is called with creativeResponse namespace and invalid responseKey with default value then default value should be retrieved`() {
        // Act
        val value: BindData =
            dataBinding.bindValue("%^DATA.creativeResponse.shortLabel|defaultValue^%", "neutral", offer)

        // Assert
        assertTrue(value is BindData.Value && value.text.equals("defaultValue"))
    }

    @Test
    fun `when bind data is called with creativeCopy namespace then correct value should be retrieved`() {
        // Act
        val value: BindData =
            dataBinding.bindValue("%^DATA.creativeCopy.disclaimer|defaultValue^%", offerModel = offer)

        // Assert
        assertTrue(value is BindData.Value && value.text.equals("test disclaimer"))
    }

    @Test
    fun `when bind data is called with creativeCopy namespace and some other copy then correct value should be retrieved`() {
        // Act
        val value: BindData =
            dataBinding.bindValue(
                "This is %^DATA.creativeCopy.disclaimer|defaultValue^% and %^DATA.creativeResponse.shortLabel|defaultValue^% he \n sd %^DATA.creativeCopy.wrongKey|DATA.creativeCopy.disclaimer|defaultValue^%",
                contextKey = "positive",
                offerModel = offer,
            )

        // Assert
        assertTrue(
            value is BindData.Value &&
                value.text.equals(
                    "This is test disclaimer and Thank You he \n sd test disclaimer",
                ),
        )
    }

    @Test
    fun `when bind data is called with multiple creativeCopy namespace then correct value should be retrieved`() {
        // Act
        val value: BindData =
            dataBinding.bindValue(
                "%^DATA.creativeCopy.wrongKey|DATA.creativeCopy.disclaimer|defaultValue^%",
                offerModel = offer,
            )

        // Assert
        assertTrue(value is BindData.Value && value.text.equals("test disclaimer"))
    }

    @Test
    fun `when bind data is called with creativeCopy namespace and invalid pathkey then undefined bindValue is returned`() {
        // Act
        val value = dataBinding.bindValue("%^DATA.creativeCopy.wrongKey^%", offerModel = offer)

        // Assert
        assertEquals(value, BindData.Undefined)
    }

    @Test
    fun `when bind model is called with valid input key and ResponseOption class then correct value should be retrieved`() {
        // Act
        val value =
            bindModel<ResponseOptionModel>(
                "positive",
                offerModel = offer,
            )

        // Assert
        assertThat(value, `is`(notNullValue()))
        assertThat(value, `is`(instanceOf(ResponseOptionModel::class.java)))
        assertThat((value as ResponseOptionModel).properties[TypedKey<String>("instanceGuid")], `is`("positiveGuid0"))
    }

    @Test
    fun `when bind model is called with invalid input key and ResponseOption class then null should be retrieved`() {
        // Act
        val value =
            bindModel<ResponseOptionModel>(
                "invalidkey",
                offerModel = offer,
            )

        // Assert
        assertThat(value, `is`(nullValue()))
    }

    @Test
    fun `when bind model is called with add-to-cart module name, valid input key and OfferImageModel class then correct value should be retrieved`() {
        // Act
        val value =
            bindModel<OfferImageModel>(
                "catalogItemImage1",
                offerModel = offer,
                module = Module.AddToCart,
                itemIndex = 0,
            )

        // Assert
        assertThat(value, `is`(notNullValue()))
        assertThat(value, `is`(instanceOf(OfferImageModel::class.java)))
        assertThat(value, `is`(OfferImageModel("light1", "dark1", "alt1", "title1")))
    }

    @Test
    fun `when bind model is called with add-to-cart module name, valid invalid key and OfferImageModel class then null should be retrieved`() {
        // Act
        val value =
            bindModel<OfferImageModel>(
                "invalidCatalogItemImage1",
                offerModel = offer,
                module = Module.AddToCart,
                itemIndex = 0,
            )

        // Assert
        assertThat(value, `is`(nullValue()))
    }

    @Test
    @Parameters(method = "getDataBindingParameters")
    fun testDataValueBinding(input: String, output: String, contextKey: String?, clazz: Class<*>, itemIndex: Int = 0) {
        println("$input $output $contextKey $clazz")

        val result = dataBinding.bindValue(input, contextKey, offer, itemIndex)

        assertThat(result, instanceOf(clazz))
        if (result is BindData.Value) {
            assertThat(result.text, `is`(output))
        }
    }

    fun getDataBindingParameters() = arrayOf(
        arrayOf(
            "Test",
            "Test",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeCopy.wrongKey|^%",
            "",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeCopy.wrongKey|default^%",
            "default",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Test%^DATA.creativeCopy.wrongKey|^% data",
            "Test data",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeCopy.disclaimer^%",
            "test disclaimer",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeResponse.shortLabel^%",
            "Thank You",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.disclaimer^% middle %^DATA.creativeCopy.title^% test-value",
            "Hello test disclaimer middle test title test-value",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.invalid|default1^% middle %^DATA.creativeCopy.anotherInvalid|default2^% test-value",
            "Hello default1 middle default2 test-value",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.invalid|DATA.creativeCopy.disclaimer^% middle %^DATA.creativeCopy.anotherInvalid|DATA.creativeCopy.title^% test-value",
            "Hello test disclaimer middle test title test-value",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Some prefix %^DATA.creativeCopy.disclaimer^% some middle %^DATA.creativeResponse.shortLabel^% some suffix",
            "Some prefix test disclaimer some middle Thank You some suffix",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Some prefix %^DATA.creativeCopy.disclaimer^% some middle %^DATA.creativeResponse.wrongKey|^%some suffix",
            "Some prefix test disclaimer some middle some suffix",
            "positive",
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^STATE.IndicatorPosition^%",
            "",
            null,
            BindData.State::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeLink.termsCondition^%",
            "<a href=\"https://rokt.com\">Terms</a>",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Some text <b>%^DATA.creativeLink.termsCondition^%</b>",
            "Some text <b><a href=\"https://rokt.com\">Terms</a></b>",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.creativeLink.invalid|something^%",
            "something",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^WRONG.creativeLink.invalid|something^%",
            "%^WRONG.creativeLink.invalid|something^%",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.catalogItem.positiveResponseText1|something^%",
            "Add to order 1",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.catalogItem.negativeResponseText1|something^%",
            "Dismiss 1",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "%^DATA.catalogItem.positiveResponseText2|something^%",
            "Add to order 2",
            null,
            BindData.Value::class.java,
            1,
        ),
        arrayOf(
            "%^DATA.catalogItem.negativeResponseText2|something^%",
            "Dismiss 2",
            null,
            BindData.Value::class.java,
            1,
        ),
        arrayOf(
            "%^DATA.catalogItem.wrongKey|something^%",
            "something",
            null,
            BindData.Value::class.java,
            1,
        ),
        arrayOf(
            "%^STATE.Something^%",
            "",
            null,
            BindData.Undefined::class.java,
            0,
        ),
        arrayOf(
            "%^STATE.TotalOffers^%",
            "%^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Offer count %^STATE.TotalOffers|^%.",
            "Offer count %^TOTAL_OFFERS^%.",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Current offer is %^STATE.IndicatorPosition^%",
            "Current offer is %^CURRENT_OFFER^%",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Current offer is %^STATE.indicatorPosition^%",
            "Current offer is %^CURRENT_OFFER^%",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Offers %^STATE.IndicatorPosition|^% of %^STATE.TotalOffers|^%",
            "Offers %^CURRENT_OFFER^% of %^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
            0,
        ),
        arrayOf(
            "Offers %^STATE.indicatorPosition|^% of %^STATE.totalOffers|^%",
            "Offers %^CURRENT_OFFER^% of %^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
            0,
        ),
    )
}
