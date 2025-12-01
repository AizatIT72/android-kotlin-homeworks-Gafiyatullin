package ru.itis.android.homework5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private var currentJobs: List<Job> = emptyList()
    private var cancelledCoroutinesCount = 0

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CoroutineCountChanged -> {
                _uiState.update { it.copy(coroutineCount = event.count) }
            }
            is MainEvent.DispatcherChanged -> {
                _uiState.update { it.copy(selectedDispatcher = event.dispatcher) }
            }
            is MainEvent.SequentialChanged -> {
                _uiState.update {
                    it.copy(
                        isSequential = event.isSequential,
                        isParallel = !event.isSequential
                    )
                }
            }
            is MainEvent.ParallelChanged -> {
                _uiState.update {
                    it.copy(
                        isParallel = event.isParallel,
                        isSequential = !event.isParallel
                    )
                }
            }
            is MainEvent.LazyChanged -> {
                _uiState.update { it.copy(isLazy = event.isLazy) }
            }
            MainEvent.LaunchClicked -> {
                launchCoroutines()
            }
            MainEvent.CancelClicked -> {
                cancelCoroutines()
            }
        }
    }

    private fun launchCoroutines() {
        val state = _uiState.value
        val count = state.coroutineCount.toInt()

        _uiState.update {
            it.copy(
                isRunning = true,
                showCancel = true,
                completedCoroutines = 0,
                totalCoroutines = count
            )
        }
        cancelledCoroutinesCount = 0

        currentJobs = if (state.isSequential) {
            launchSequentialCoroutines(count, state.selectedDispatcher, state.isLazy)
        } else {
            launchParallelCoroutines(count, state.selectedDispatcher, state.isLazy)
        }

        viewModelScope.launch {
            currentJobs.joinAll()
            _uiState.update {
                it.copy(
                    isRunning = false,
                    showCancel = false,
                    completedCoroutines = 0,
                    totalCoroutines = 0
                )
            }
        }
    }

    private fun launchSequentialCoroutines(
        count: Int,
        dispatcher: DispatcherType,
        isLazy: Boolean
    ): List<Job> {
        val jobs = mutableListOf<Job>()

        val sequentialJob = viewModelScope.launch(getDispatcher(dispatcher)) {
            for (i in 0 until count) {
                val job = launch(
                    context = getDispatcher(dispatcher),
                    start = if (isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
                ) {
                    try {
                        executeHeavyOperation(i)
                        _uiState.update { state ->
                            state.copy(completedCoroutines = state.completedCoroutines + 1)
                        }
                    } catch (e: ToastException) {
                        _toastMessage.emit(e.message ?: "Operation took too long!")
                        _uiState.update { state ->
                            state.copy(completedCoroutines = state.completedCoroutines + 1)
                        }
                    } catch (e: SnackbarException) {
                        _snackbarMessage.emit(e.message ?: "Operation timeout!")
                        _uiState.update { state ->
                            state.copy(completedCoroutines = state.completedCoroutines + 1)
                        }
                    } catch (e: ResetException) {
                        resetToDefaultSettings()
                        _toastMessage.emit(e.message ?: "Reset required!")
                        _uiState.update { state ->
                            state.copy(completedCoroutines = state.completedCoroutines + 1)
                        }
                    } catch (e: Exception) {
                        _toastMessage.emit("Unexpected error: ${e.message}")
                        _uiState.update { state ->
                            state.copy(completedCoroutines = state.completedCoroutines + 1)
                        }
                    }
                }

                if (isLazy) {
                    job.start()
                }

                jobs.add(job)
                job.join()
            }
        }

        jobs.add(sequentialJob)
        return jobs
    }

    private fun launchParallelCoroutines(
        count: Int,
        dispatcher: DispatcherType,
        isLazy: Boolean
    ): List<Job> {
        val jobs = mutableListOf<Job>()

        for (i in 0 until count) {
            val job = viewModelScope.launch(
                context = getDispatcher(dispatcher),
                start = if (isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
            ) {
                try {
                    executeHeavyOperation(i)
                    _uiState.update { state ->
                        state.copy(completedCoroutines = state.completedCoroutines + 1)
                    }
                } catch (e: ToastException) {
                    _toastMessage.emit(e.message ?: "Operation took too long!")
                    _uiState.update { state ->
                        state.copy(completedCoroutines = state.completedCoroutines + 1)
                    }
                } catch (e: SnackbarException) {
                    _snackbarMessage.emit(e.message ?: "Operation timeout!")
                    _uiState.update { state ->
                        state.copy(completedCoroutines = state.completedCoroutines + 1)
                    }
                } catch (e: ResetException) {
                    resetToDefaultSettings()
                    _toastMessage.emit(e.message ?: "Reset required!")
                    _uiState.update { state ->
                        state.copy(completedCoroutines = state.completedCoroutines + 1)
                    }
                } catch (e: Exception) {
                    _toastMessage.emit("Unexpected error: ${e.message}")
                    _uiState.update { state ->
                        state.copy(completedCoroutines = state.completedCoroutines + 1)
                    }
                }
            }

            if (isLazy) {
                job.start()
            }

            jobs.add(job)
        }

        return jobs
    }

    private suspend fun executeHeavyOperation(index: Int) {
        val delayTime = (Constants.MIN_DELAY..Constants.MAX_DELAY).random()
        delay(delayTime)

        if (delayTime >= Constants.LONG_OPERATION_THRESHOLD &&
            (1..100).random() <= Constants.EXCEPTION_CHANCE) {
            throwRandomException()
        }
    }

    private fun throwRandomException() {
        val exceptionType = (0..2).random()
        when (exceptionType) {
            0 -> throw ToastException()
            1 -> throw SnackbarException()
            2 -> throw ResetException()
        }
    }

    private fun resetToDefaultSettings() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDispatcher = DispatcherType.Default,
                isSequential = true,
                isParallel = false,
                isLazy = false
            )
        }
    }

    private fun getDispatcher(type: DispatcherType): CoroutineDispatcher {
        return when (type) {
            DispatcherType.Default -> Dispatchers.Default
            DispatcherType.IO -> Dispatchers.IO
            DispatcherType.Main -> Dispatchers.Main
        }
    }

    private fun cancelCoroutines() {
        currentJobs.forEach { job ->
            if (job.isActive) {
                job.cancel()
                cancelledCoroutinesCount++
            }
        }

        viewModelScope.launch {
            _toastMessage.emit("Cancelled $cancelledCoroutinesCount coroutines")
            _uiState.update {
                it.copy(
                    isRunning = false,
                    showCancel = false,
                    completedCoroutines = 0,
                    totalCoroutines = 0
                )
            }
        }
    }
}