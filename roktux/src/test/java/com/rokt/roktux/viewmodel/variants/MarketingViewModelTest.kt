package com.rokt.roktux.viewmodel.variants

import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.modelmapper.uimodel.PluginModel
import com.rokt.modelmapper.uimodel.SlotModel
import com.rokt.roktux.viewmodel.BaseViewModelTest
import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.layout.LayoutContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class MarketingViewModelTest : BaseViewModelTest() {

    private val slot: SlotModel = mockk(relaxed = true)
    private val modelMapper: ModelMapper = mockk {
        every { getSavedExperience()?.plugins } returns listOf(
            mockk<PluginModel> {
                every { slots } returns listOf(slot).toImmutableList()
            },
        ).toImmutableList()
    }
    private val ioDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MarketingViewModel

    @Before
    fun setUpTest() {
        viewModel = MarketingViewModel(0, modelMapper, ioDispatcher, emptyMap())
    }

    @Test
    fun `viewModel initialisation should set the success state with correct view state`() = runTest {
        // Given
        val layoutVariantSchema = slot.layoutVariant?.layoutVariantSchema
        val creativeCopy = slot.offer?.creative?.copy

        // When
        val viewState = viewModel.viewState.value

        // Then
        assertThat(viewState).isInstanceOf(BaseContract.BaseViewState.Success::class.java)
        val successState = viewState as BaseContract.BaseViewState.Success
        assertThat(successState.value.uiModel).isEqualTo(layoutVariantSchema)
        assertThat(successState.value.creativeCopy).isEqualTo(creativeCopy)
    }

    @Test
    fun `SetCustomState event should update the custom state and propagate the event`() = runTest {
        // Given
        val key = "key"
        val value = 1

        // When
        viewModel.setEvent(LayoutContract.LayoutEvent.SetCustomState(key, value))
        val viewState = viewModel.viewState.value
        val effect = viewModel.effect.first()

        // Then
        val successState = viewState as BaseContract.BaseViewState.Success
        assertThat(successState.value.customState).containsEntry(key, value)
        assertThat(effect).isInstanceOf(MarketingVariantContract.LayoutVariantEffect.PropagateEvent::class.java)
        val propagateEvent = effect as MarketingVariantContract.LayoutVariantEffect.PropagateEvent
        assertThat(propagateEvent.event).isEqualTo(
            LayoutContract.LayoutEvent.SetOfferCustomState(
                0,
                mapOf(key to value),
            ),
        )
    }

    @Test
    fun `UserInteracted should set the SetSignalViewed effect only once`() = runTest {
        // Given
        val event = LayoutContract.LayoutEvent.UserInteracted

        // When
        viewModel.setEvent(event)
        viewModel.setEvent(event)
        val effects = mutableListOf<MarketingVariantContract.LayoutVariantEffect>()
        val job = launch {
            viewModel.effect.collect { effects.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertThat(effects).hasSize(1)
        assertThat(effects[0]).isEqualTo(MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(0))
    }

    @Test
    fun `OfferVisibilityChanged should trigger SetSignalViewed effect if the visibility was not changed within 1 second`() = runTest(ioDispatcher) {
        // Given
        val event = LayoutContract.LayoutEvent.OfferVisibilityChanged(0, true)

        // When
        viewModel.setEvent(event)
        advanceUntilIdle()
        val effect = viewModel.effect.first()

        // Then
        assertThat(effect).isEqualTo(MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(0))
    }

    @Test
    fun `OfferVisibilityChanged should not trigger SetSignalViewed effect when the visibility changed within 1 second`() = runTest(ioDispatcher) {
        // Given
        val event = LayoutContract.LayoutEvent.OfferVisibilityChanged(0, true)

        // When
        viewModel.setEvent(event)
        viewModel.setEvent(event.copy(visible = false))
        val effects = mutableListOf<MarketingVariantContract.LayoutVariantEffect>()
        val job = launch {
            viewModel.effect.collect { effects.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertThat(effects).isEmpty()
    }

    @Test
    fun `OfferVisibilityChanged should not trigger SetSignalViewed if it was already triggered by UserInteracted event`() = runTest(ioDispatcher) {
        // Given
        val offerVisibilityEvent = LayoutContract.LayoutEvent.OfferVisibilityChanged(0, true)
        val userInteractedEvent = LayoutContract.LayoutEvent.UserInteracted

        // When
        viewModel.setEvent(offerVisibilityEvent)
        viewModel.setEvent(userInteractedEvent)
        val effects = mutableListOf<MarketingVariantContract.LayoutVariantEffect>()
        val job = launch {
            viewModel.effect.collect { effects.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // Then
        assertThat(effects).hasSize(1)
        assertThat(effects[0]).isEqualTo(MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(0))
    }

    @Test
    fun `Other LayoutEvents should be propagated as LayoutVariantEffect`() = runTest {
        // Given
        val event = LayoutContract.LayoutEvent.FirstOfferLoaded

        // When
        viewModel.setEvent(event)
        val effect = viewModel.effect.first()

        // Then
        assertThat(effect).isEqualTo(MarketingVariantContract.LayoutVariantEffect.PropagateEvent(event))
    }
}
