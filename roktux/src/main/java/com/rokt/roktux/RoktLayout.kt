package com.rokt.roktux

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rokt.modelmapper.utils.FIRST_OFFER_INDEX
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.di.layout.LocalFontFamilyProvider
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.event.RoktPlatformEventsWrapper
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.InternalActivityResultContract
import com.rokt.roktux.utils.calculateBreakpoint
import com.rokt.roktux.utils.findActivity
import com.rokt.roktux.utils.getScreenHeightInPixels
import com.rokt.roktux.utils.isSystemInDarkMode
import com.rokt.roktux.utils.layoutExitAnimationModifier
import com.rokt.roktux.utils.openUrl
import com.rokt.roktux.utils.userInteractionDetector
import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.component.DIComponentViewModel
import com.rokt.roktux.viewmodel.composablescoped.WithComposableScopedViewModelStoreOwner
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.LayoutViewModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

/**
 * Composable function to render the Rokt layout.
 *
 * @param experienceResponse The response string containing the experience data.
 * @param location The location identifier for the layout.
 * @param modifier The modifier to be applied to the layout.
 * @param roktUxConfig The configuration for the Rokt UX.
 * @param onUxEvent Callback for UX events.
 * @param onPlatformEvent Callback for platform events.
 */
@Composable
fun RoktLayout(
    experienceResponse: String,
    location: String,
    modifier: Modifier = Modifier,
    roktUxConfig: RoktUxConfig,
    onUxEvent: (event: RoktUxEvent) -> Unit = { },
    onPlatformEvent: (platformEvents: RoktPlatformEventsWrapper) -> Unit = { },
) {
    val experienceHash = experienceResponse.hashCode().toString()
    val context = LocalContext.current
    val imageLoader = roktUxConfig.imageHandlingStrategy.getImageLoader(context)
    var currentOffer by rememberSaveable(key = experienceHash) {
        roktUxConfig.viewState?.offerIndex?.let { mutableIntStateOf(it) } ?: mutableIntStateOf(FIRST_OFFER_INDEX)
    }
    var customState by rememberSaveable(
        key = experienceHash,
        saver = mapSaver(
            save = { it.value },
            restore = {
                mutableStateOf(
                    it.filterValues { value -> value is Int }.mapValues { (_, value) -> value as Int },
                )
            },
        ),
    ) {
        roktUxConfig.viewState?.customStates?.let { mutableStateOf(it) } ?: mutableStateOf(mapOf())
    }
    val offerCustomStates by remember {
        roktUxConfig.viewState?.offerCustomStates?.let { mutableStateOf(it) }
            ?: mutableStateOf(mapOf())
    }
    if (LocalViewModelStoreOwner.current != null && roktUxConfig.viewState?.pluginDismissed != true) {
        WithComposableScopedViewModelStoreOwner(key = experienceHash) {
            val viewModel = viewModel<DIComponentViewModel>(
                factory = DIComponentViewModel.DIComponentViewModelFactory(
                    experienceResponse = experienceResponse,
                    location = location,
                    uxEvent = onUxEvent,
                    platformEvent = { events ->
                        onPlatformEvent(
                            RoktPlatformEventsWrapper(
                                integration = RoktUx.getIntegrationConfig(
                                    context,
                                ),
                                events = events,
                            ),
                        )
                    },
                    viewStateChange = { state ->
                        roktUxConfig.viewStateChange?.invoke(state)
                    },
                    imageLoader = imageLoader,
                    currentOffer = currentOffer,
                    customStates = customState,
                    offerCustomStates = offerCustomStates,
                    handleUrlByApp = roktUxConfig.handleUrlByApp,
                ),
            )
            DIComponentInjector(
                viewModel = viewModel,
                modifier = modifier,
                fontMap = roktUxConfig.composeFontMap?.toImmutableMap() ?: persistentMapOf(),
                colorMode = roktUxConfig.colorMode,
                updateSavedState = { offer, state ->
                    currentOffer = offer
                    customState = state
                },
            )
        }
    }
}

@Composable
private fun DIComponentInjector(
    viewModel: DIComponentViewModel,
    modifier: Modifier = Modifier,
    fontMap: ImmutableMap<String, FontFamily>,
    colorMode: ColorMode? = null,
    updateSavedState: (currentOffer: Int, customState: Map<String, Int>) -> Unit,
) {
    CompositionLocalProvider(
        LocalLayoutComponent provides viewModel.component,
    ) {
        WithComposableScopedViewModelStoreOwner(key = viewModel.component) {
            val layoutViewModel = viewModel<LayoutViewModel>(
                factory = viewModel.component[LayoutViewModel.RoktViewModelFactory::class.java],
            )
            RoktLayout(
                viewModel = layoutViewModel,
                modifier = modifier,
                fontMap = fontMap,
                colorMode = colorMode,
                updateSavedState = updateSavedState,
            )
        }
    }
}

@Composable
private fun RoktLayout(
    viewModel: LayoutViewModel,
    modifier: Modifier = Modifier,
    fontMap: ImmutableMap<String, FontFamily>,
    colorMode: ColorMode? = null,
    updateSavedState: (currentOffer: Int, customState: Map<String, Int>) -> Unit,
) {
    val component = LocalLayoutComponent.current
    val context = LocalContext.current
    val factory = remember { component[LayoutUiModelFactory::class.java] }
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var animateLayoutExit by rememberSaveable {
        mutableStateOf(AnimationState.Show)
    }
    var visible by rememberSaveable {
        mutableStateOf(true)
    }
    val customTabContract = remember {
        InternalActivityResultContract()
    }
    val customTabLauncher = rememberLauncherForActivityResult(contract = customTabContract) { result ->
        result()
    }
    var currentUrlEffect: LayoutContract.LayoutEffect.OpenUrlExternal? by remember { mutableStateOf(null) }
    // Register ActivityLifecycleCallbacks
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                currentUrlEffect?.let { effect ->
                    effect.onClose(effect.id)
                    currentUrlEffect = null
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                LayoutContract.LayoutEffect.CloseLayout -> {
                    animateLayoutExit = AnimationState.Hide
                }

                is LayoutContract.LayoutEffect.OpenUrlExternal -> {
                    currentUrlEffect = effect
                    context.openUrl(
                        effect.url,
                        effect.id,
                        effect.onClose,
                    ) { throwable ->
                        currentUrlEffect = null
                        effect.onError(effect.id, throwable)
                    }
                }

                is LayoutContract.LayoutEffect.OpenUrlInternal -> {
                    customTabLauncher.launch(effect)
                }
            }
        }.collect()
    }
    when (val state = viewState) {
        BaseContract.BaseViewState.Empty -> {
        }

        is BaseContract.BaseViewState.Error -> {
        }

        is BaseContract.BaseViewState.Success -> {
            if (visible) {
                LaunchedEffect(Unit) {
                    viewModel.setEvent(LayoutContract.LayoutEvent.LayoutReady)
                }
                updateSavedState(state.value.offerUiState.currentOfferIndex, state.value.offerUiState.customState)
                var isLayoutInteractive by remember { mutableStateOf(false) }
                var hasUserInteracted by remember { mutableStateOf(false) }
                CompositionLocalProvider(
                    LocalFontFamilyProvider provides (
                        persistentMapOf(
                            ROKT_ICONS_FONT_FAMILY to FontFamily(
                                Font(resId = R.font.rokt_icons),
                            ),
                        ) + fontMap
                        ).toImmutableMap(),
                ) {
                    factory.CreateComposable(
                        model = state.value.model,
                        modifier = Modifier
                            .userInteractionDetector(
                                state.value.model,
                            ) {
                                hasUserInteracted = true
                            }
                            .onGloballyPositioned {
                                isLayoutInteractive = true
                            }
                            .layoutExitAnimationModifier(
                                state.value.model,
                                animateLayoutExit,
                                (LocalContext.current.findActivity()).getScreenHeightInPixels(),
                            ) {
                                visible = false
                            }
                            .then(modifier),
                        isPressed = false,
                        offerState = state.value.offerUiState,
                        isDarkModeEnabled = isSystemInDarkMode(colorMode),
                        breakpointIndex = (LocalContext.current.findActivity()).calculateBreakpoint(
                            breakpoints = state.value.offerUiState.breakpoints,
                        ),
                    ) { event ->
                        viewModel.setEvent(event = event)
                    }
                }

                if (isLayoutInteractive) {
                    LaunchedEffect(Unit) {
                        viewModel.setEvent(LayoutContract.LayoutEvent.LayoutInteractive)
                    }
                }

                if (hasUserInteracted) {
                    LaunchedEffect(Unit) {
                        viewModel.setEvent(LayoutContract.LayoutEvent.UserInteracted)
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.setEvent(LayoutContract.LayoutEvent.LayoutInitialised)
    }
}
