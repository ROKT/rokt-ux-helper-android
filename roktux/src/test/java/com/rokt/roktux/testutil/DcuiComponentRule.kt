package com.rokt.roktux.testutil

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.text.font.FontFamily
import com.core.testutils.BaseComponentRule
import com.core.testutils.annotations.DcuiBreakpoint
import com.core.testutils.annotations.DcuiNodeComponentState
import com.core.testutils.annotations.TestPseudoState
import com.core.testutils.annotations.WindowSize
import com.rokt.modelmapper.data.DataBindingImpl
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.di.layout.LayoutComponent
import com.rokt.roktux.di.layout.LocalFontFamilyProvider
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.imagehandler.NetworkStrategy
import com.rokt.roktux.utils.getBreakpointIndex
import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

class DcuiComponentRule(val composeTestRule: ComposeContentTestRule) : BaseComponentRule(composeTestRule, true) {

    lateinit var uiModel: LayoutSchemaUiModel

    internal val capturedEvents = mutableListOf<BaseContract.BaseEvent>()

    override fun initializeResponseToUiModel(response: String, testInInnerLayout: Boolean, isDarkModeEnabled: Boolean) {
        if (testInInnerLayout) {
            uiModel =
                ExperienceModelMapperImpl(response, DataBindingImpl()).transformResponse()
                    .getOrThrow().plugins[0].slots[0].layoutVariant?.layoutVariantSchema!!
        } else {
            uiModel =
                ExperienceModelMapperImpl(response, DataBindingImpl()).transformResponse()
                    .getOrThrow().plugins[0].outerLayoutSchema!!
        }
    }

    override fun loadComponent(
        testTag: String,
        dcuiNodeComponentState: DcuiNodeComponentState?,
        windowSize: WindowSize,
        breakpointIndex: Int,
        pseudoState: TestPseudoState,
        isDarkModeEnabled: Boolean,
    ) {
        val factory = LayoutUiModelFactory()
        val breakpoints = buildBreakpoints(dcuiNodeComponentState?.breakpoints)
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalLayoutComponent provides LayoutComponent(
                    "",
                    "",
                    {},
                    {},
                    NetworkStrategy().getImageLoader(LocalContext.current),
                    true,
                    0,
                    mapOf(),
                ),
                LocalFontFamilyProvider provides persistentMapOf("roboto" to FontFamily.Default),
            ) {
                factory.CreateComposable(
                    model = uiModel,
                    modifier = Modifier.testTag(testTag),
                    isPressed = pseudoState.isPressed,
                    offerState = OfferUiState(
                        currentOfferIndex = dcuiNodeComponentState?.currentOffer ?: 0,
                        lastOfferIndex = dcuiNodeComponentState?.totalOffer?.let { it - 1 } ?: 0,
                        viewableItems = dcuiNodeComponentState?.viewableItems?.getOrNull(
                            getBreakpointIndex(
                                windowSize.width,
                                breakpoints,
                            ),
                        ) ?: 1,
                        creativeCopy = dcuiNodeComponentState?.creativeCopy?.associateBy(
                            { it.key },
                            { it.value },
                        )?.toImmutableMap() ?: persistentMapOf(),
                        breakpoints = breakpoints,
                        customState = persistentMapOf(),
                    ),
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = getBreakpointIndex(windowSize.width, breakpoints),
                ) {
                    capturedEvents.add(it)
                }
            }
        }
    }

    private fun buildBreakpoints(dcuiBreakpoints: Array<DcuiBreakpoint>?): ImmutableMap<String, Int> {
        val breakpoints = mutableMapOf<String, Int>()
        // Add the default breakpoint
        breakpoints["default"] = 0
        // Add the other breakpoints
        dcuiBreakpoints?.associateBy({ it.key }, { it.value })?.let {
            breakpoints.putAll(
                it,
            )
        }
        return breakpoints.toImmutableMap()
    }
}
