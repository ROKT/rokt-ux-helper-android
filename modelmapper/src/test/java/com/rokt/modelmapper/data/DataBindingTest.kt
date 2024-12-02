package com.rokt.modelmapper.data

import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.testutils.MockkUnitTest
import com.rokt.modelmapper.uimodel.CreativeLink
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.collections.immutable.persistentMapOf
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
    @Parameters(method = "getDataBindingParameters")
    fun testDataValueBinding(input: String, output: String, contextKey: String?, clazz: Class<*>) {
        println("$input $output $contextKey $clazz")

        val result = dataBinding.bindValue(input, contextKey, offer)

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
        ),
        arrayOf(
            "%^DATA.creativeCopy.wrongKey|^%",
            "",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^DATA.creativeCopy.wrongKey|default^%",
            "default",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Test%^DATA.creativeCopy.wrongKey|^% data",
            "Test data",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^DATA.creativeCopy.disclaimer^%",
            "test disclaimer",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^DATA.creativeResponse.shortLabel^%",
            "Thank You",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.disclaimer^% middle %^DATA.creativeCopy.title^% test-value",
            "Hello test disclaimer middle test title test-value",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.invalid|default1^% middle %^DATA.creativeCopy.anotherInvalid|default2^% test-value",
            "Hello default1 middle default2 test-value",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "Hello %^DATA.creativeCopy.invalid|DATA.creativeCopy.disclaimer^% middle %^DATA.creativeCopy.anotherInvalid|DATA.creativeCopy.title^% test-value",
            "Hello test disclaimer middle test title test-value",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "Some prefix %^DATA.creativeCopy.disclaimer^% some middle %^DATA.creativeResponse.shortLabel^% some suffix",
            "Some prefix test disclaimer some middle Thank You some suffix",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "Some prefix %^DATA.creativeCopy.disclaimer^% some middle %^DATA.creativeResponse.wrongKey|^%some suffix",
            "Some prefix test disclaimer some middle some suffix",
            "positive",
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^STATE.IndicatorPosition^%",
            "",
            null,
            BindData.State::class.java,
        ),
        arrayOf(
            "%^DATA.creativeLink.termsCondition^%",
            "<a href=\"https://rokt.com\">Terms</a>",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Some text <b>%^DATA.creativeLink.termsCondition^%</b>",
            "Some text <b><a href=\"https://rokt.com\">Terms</a></b>",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^DATA.creativeLink.invalid|something^%",
            "something",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^WRONG.creativeLink.invalid|something^%",
            "%^WRONG.creativeLink.invalid|something^%",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "%^STATE.Something^%",
            "",
            null,
            BindData.Undefined::class.java,
        ),
        arrayOf(
            "%^STATE.TotalOffers^%",
            "%^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Offer count %^STATE.TotalOffers|^%.",
            "Offer count %^TOTAL_OFFERS^%.",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Current offer is %^STATE.IndicatorPosition^%",
            "Current offer is %^CURRENT_OFFER^%",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Current offer is %^STATE.indicatorPosition^%",
            "Current offer is %^CURRENT_OFFER^%",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Offers %^STATE.IndicatorPosition|^% of %^STATE.TotalOffers|^%",
            "Offers %^CURRENT_OFFER^% of %^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
        ),
        arrayOf(
            "Offers %^STATE.indicatorPosition|^% of %^STATE.totalOffers|^%",
            "Offers %^CURRENT_OFFER^% of %^TOTAL_OFFERS^%",
            null,
            BindData.Value::class.java,
        ),
    )
}
