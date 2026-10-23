package com.rokt.roktux.component

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.modelmapper.uimodel.WhenUiTransition
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertEquals
import org.junit.Test

class HostsSubcompositionTest {

    @Test
    fun testCarouselsGalleriesAndCarouselDistributionsAlwaysHostSubcomposition() {
        listOf(carousel, gallery, carouselDistribution).forEach { model ->
            assertHosts(model, offerLayoutsHostSubcomposition = true, expected = true)
            assertHosts(model, offerLayoutsHostSubcomposition = false, expected = true)
        }
    }

    @Test
    fun testDistributionsAndLayoutVariantsHostSubcompositionOnlyWhenTheirOfferLayoutsDo() {
        listOf(oneByOne, grouped, marketing).forEach { model ->
            assertHosts(model, offerLayoutsHostSubcomposition = true, expected = true)
            assertHosts(model, offerLayoutsHostSubcomposition = false, expected = false)
        }
    }

    @Test
    fun testContainersHostSubcompositionWhenAChildDoes() {
        containersAround(carousel).forEach { model ->
            assertHosts(model, offerLayoutsHostSubcomposition = false, expected = true)
        }
        containersAround(oneByOne).forEach { model ->
            assertHosts(model, offerLayoutsHostSubcomposition = true, expected = true)
            assertHosts(model, offerLayoutsHostSubcomposition = false, expected = false)
        }
        containersAround(text).forEach { model ->
            assertHosts(model, offerLayoutsHostSubcomposition = true, expected = false)
            assertHosts(model, offerLayoutsHostSubcomposition = false, expected = false)
        }
    }

    @Test
    fun testLeafDoesNotHostSubcomposition() {
        assertHosts(text, offerLayoutsHostSubcomposition = true, expected = false)
        assertHosts(text, offerLayoutsHostSubcomposition = false, expected = false)
    }

    private fun assertHosts(model: LayoutSchemaUiModel, offerLayoutsHostSubcomposition: Boolean, expected: Boolean) {
        assertEquals(
            "${model::class.simpleName} with offerLayoutsHostSubcomposition=$offerLayoutsHostSubcomposition",
            expected,
            model.hostsSubcomposition(offerLayoutsHostSubcomposition),
        )
    }

    private fun containersAround(child: LayoutSchemaUiModel): List<LayoutSchemaUiModel> {
        val children = persistentListOf(text, child)
        val column = LayoutSchemaUiModel.ColumnUiModel(null, null, null, isScrollable = false, children = children)
        return listOf(
            LayoutSchemaUiModel.RowUiModel(null, null, null, isScrollable = false, children = children),
            column,
            LayoutSchemaUiModel.BoxUiModel(null, null, null, children = children),
            LayoutSchemaUiModel.WhenUiModel(
                predicates = persistentListOf(),
                children = children,
                transition = WhenUiTransition(),
                hide = null,
            ),
            LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel(null, null, null, children = children, customStateKey = ""),
            LayoutSchemaUiModel.CatalogStackedCollectionUiModel(null, null, null, children = children),
            LayoutSchemaUiModel.CatalogCombinedCollectionUiModel(
                null,
                null,
                null,
                childrenByCatalogItem = persistentMapOf(0 to persistentListOf(text), 1 to children),
            ),
            LayoutSchemaUiModel.OverlayUiModel(null, null, null, allowBackdropToClose = false, child = column),
            LayoutSchemaUiModel.BottomSheetUiModel(null, null, null, allowBackdropToClose = false, child = column),
        )
    }

    private val text = LayoutSchemaUiModel.BasicTextUiModel(null, null, null, null, null, BindData.Value("text"))

    private val carousel = LayoutSchemaUiModel.DataImageCarouselUiModel(
        null,
        null,
        null,
        images = emptyMap(),
        duration = 0,
        indicatorStyle = null,
        indicator = null,
        transition = null,
        activeIndicator = null,
        seenIndicator = null,
        progressIndicatorContainer = null,
        customStateKey = "",
    )

    private val gallery = LayoutSchemaUiModel.CatalogImageGalleryUiModel(
        null,
        null,
        null,
        images = persistentMapOf(),
        showIndicators = false,
        indicatorStyle = null,
        activeIndicator = null,
        seenIndicator = null,
        progressIndicatorContainer = null,
        controlButton = null,
        customStateKey = "",
        backwardIcon = null,
        forwardIcon = null,
        a11yLabel = null,
    )

    private val carouselDistribution = LayoutSchemaUiModel.CarouselDistributionUiModel(
        null,
        null,
        null,
        viewableItems = persistentListOf(1),
        peekThroughSizeUiModel = persistentListOf(),
    )

    private val oneByOne = LayoutSchemaUiModel.OneByOneDistributionUiModel(
        null,
        null,
        null,
        transition = TransitionUiModel.FadeInOutTransition(0),
    )

    private val grouped = LayoutSchemaUiModel.GroupedDistributionUiModel(
        null,
        null,
        null,
        viewableItems = persistentListOf(1),
        transition = TransitionUiModel.FadeInOutTransition(0),
    )

    private val marketing = LayoutSchemaUiModel.MarketingUiModel()
}
