package com.rokt.roktux.viewmodel

import com.core.testutils.BaseViewModelTest
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.LayoutSettings
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.OptionsModel
import com.rokt.modelmapper.uimodel.PlacementContextModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.roktux.event.EventType
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.LayoutViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoktLayoutViewModelTest : BaseViewModelTest() {
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val mapper: ModelMapper = mockk(relaxed = true) {
        coEvery { transformResponse() } returns mockk(relaxed = true)
        every { getSavedExperience() } returns mockk(relaxed = true) {
            every { sessionId } returns "sessionId"
            every { placementContext } returns PlacementContextModel("pageInstanceGuid", "layout_token")
            every { plugins } returns persistentListOf(
                mockk(relaxed = true) {
                    every { id } returns "pluginId"
                    every { name } returns "pluginName"
                    every { targetElementSelector } returns "location1"
                    every { outerLayoutSchema } returns mockk(relaxed = true)
                    every { breakpoint } returns mockk(relaxed = true)
                    every { instanceGuid } returns "pluginInstanceGuid"
                    every { slots } returns persistentListOf(
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid"
                                }
                            }
                        },
                        mockk(relaxed = true) {
                            every { instanceGuid } returns "slotInstanceGuid1"
                            every { offer } returns mockk(relaxed = true) {
                                every { creative } returns mockk(relaxed = true) {
                                    every { instanceGuid } returns "creativeInstanceGuid1"
                                }
                            }
                        },
                    )
                    every { settings } returns LayoutSettings(closeOnComplete = true)
                },
            )
        }
    }

    private val uxEvent: (RoktUxEvent) -> Unit = mockk(relaxed = true)
    private val platformEvent: (List<RoktPlatformEvent>) -> Unit = mockk(relaxed = true)
    internal lateinit var layoutViewModel: LayoutViewModel

    @Before
    fun setup() {
        initialize()
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
    }

    private fun initialize(handleUrlByApp: Boolean = true) {
        layoutViewModel = LayoutViewModel(
            location = "location1",
            uxEvent = uxEvent,
            platformEvent = platformEvent,
            modelMapper = mapper,
            ioDispatcher = ioDispatcher,
            mainDispatcher = ioDispatcher,
            handleUrlByApp = handleUrlByApp,
            currentOffer = 0,
            customState = mapOf(),
        )
    }

    @Test
    fun `LayoutReady Event should send LayoutReady UxEvent`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutReady)
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutReady("pluginId"))
        }
    }

    @Test
    fun `LayoutInteractive Event should send LayoutInteractive UxEvent`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInteractive)
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutInteractive("pluginId"))
        }
    }

    @Test
    fun `ResponseOptionSelected Event should send UxEvents`() = runTest {
        // Arrange
        val responseOptionProperties = HMap().apply {
            this[TypedKey<Boolean>("isPositive")] = true
            this[TypedKey<Action>("action")] = Action.CaptureOnly
            this[TypedKey<SignalType>("signalType")] = SignalType.SignalResponse
            this[TypedKey<String>("instanceGuid")] = "responseInstanceGuid"
        }
        // Act
        layoutViewModel.setEvent(
            LayoutContract.LayoutEvent.ResponseOptionSelected(
                1,
                OpenLinks.Internally,
                responseOptionProperties,
                true,
            ),
        )
        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.OfferEngagement("pluginId"))
            uxEvent.invoke(RoktUxEvent.PositiveEngagement("pluginId"))
        }
    }

    @Test
    fun `SetCurrentOffer Event past the total offers should send not LayoutCompleted UxEvent and SignalDismissal platformEvent when closeOnComplete is false`() =
        runTest {
            // Arrange
            every { mapper.getSavedExperience() } returns mockk(relaxed = true) {
                every { plugins } returns persistentListOf(
                    mockk(relaxed = true) {
                        every { id } returns "pluginId"
                        every { settings } returns LayoutSettings(closeOnComplete = false)
                        every { slots } returns persistentListOf(
                            mockk(relaxed = true) {
                                every { instanceGuid } returns "slotInstanceGuid"
                                every { offer } returns mockk(relaxed = true) {
                                    every { creative } returns mockk(relaxed = true) {
                                        every { instanceGuid } returns "creativeInstanceGuid"
                                    }
                                }
                            },
                            mockk(relaxed = true) {
                                every { instanceGuid } returns "slotInstanceGuid1"
                                every { offer } returns mockk(relaxed = true) {
                                    every { creative } returns mockk(relaxed = true) {
                                        every { instanceGuid } returns "creativeInstanceGuid1"
                                    }
                                }
                            },
                        )
                    },
                )
            }

            // Act
            layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
            layoutViewModel.setEvent(LayoutContract.LayoutEvent.SetCurrentOffer(2))

            // Assert
            verify(exactly = 0) {
                uxEvent.invoke(RoktUxEvent.LayoutCompleted("pluginId"))
                platformEvent.invoke(
                    match { event ->
                        event[0].eventType == EventType.SignalDismissal
                    },
                )
            }
        }

    @Test
    fun `LayoutInitialised Event when experienceModel has error should end LayoutFailure UxEvent but not diagnostic event`() =
        runTest {
            // Arrange
            every { mapper.transformResponse() } returns Result.failure(IllegalAccessException("no access"))

            // Act
            layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)

            // Assert
            verify {
                uxEvent.invoke(RoktUxEvent.LayoutFailure())
            }

            verify(exactly = 0) {
                platformEvent.invoke(
                    match { event ->
                        event[0].eventType == EventType.SignalSdkDiagnostic
                    },
                )
            }
        }

    @Test
    fun `handleError should not send error if experienceModel is initialized and useDiagnostics is false`() = runTest {
        // Arrange
        every { mapper.getSavedExperience() } returns mockk(relaxed = true) {
            every { options } returns OptionsModel(useDiagnosticEvents = false)
        }

        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
        layoutViewModel.handleError(IllegalAccessException("no access"))

        // Assert
        verify {
            uxEvent.invoke(RoktUxEvent.LayoutFailure())
        }
    }

    @Test
    fun `UrlSelected Event should send OpenUrlInternal UxEvent when no value set for handleUrlByApp`() = runTest {
        // Act
        layoutViewModel.setEvent(LayoutContract.LayoutEvent.UrlSelected("url", OpenLinks.Internally))

        // Assert
        verify {
            uxEvent.invoke(
                match { event ->
                    event::class.java == RoktUxEvent.OpenUrl::class.java &&
                        (event as RoktUxEvent.OpenUrl).url == "url"
                },
            )
        }
    }
}
