package com.rokt.roktux.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.roktux.viewmodel.base.BaseContract.BaseViewState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal abstract class BaseViewModel<Event, UiState, Effect> : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    private val _viewState = MutableStateFlow<BaseViewState<UiState>>(BaseViewState.Empty)

    val viewState = _viewState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    protected fun setSuccessState(state: UiState) = safeLaunch {
        _viewState.emit(BaseViewState.Success(state))
    }

    protected fun updateState(update: (UiState) -> UiState) {
        if (viewState.value is BaseViewState.Success) {
            val currentUiState = (viewState.value as BaseViewState.Success).value
            setSuccessState(update(currentUiState))
        }
    }

    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler, block = block)
    }

    private fun subscribeToEvents() {
        safeLaunch {
            _event.collect {
                safeLaunch { handleEvents(it) }
            }
        }
    }

    abstract suspend fun handleEvents(event: Event)

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    open fun handleError(exception: Throwable) {
        _viewState.value = BaseViewState.Error(exception)
    }
}
