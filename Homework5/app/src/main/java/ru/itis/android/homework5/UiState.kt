package ru.itis.android.homework5

data class UiState(
    val coroutineCount: Float = 10f,
    val selectedDispatcher: DispatcherType = DispatcherType.Default,
    val isSequential: Boolean = true,
    val isParallel: Boolean = false,
    val isLazy: Boolean = false,
    val isRunning: Boolean = false,
    val showCancel: Boolean = false,
    val completedCoroutines: Int = 0,
    val totalCoroutines: Int = 0
)

enum class DispatcherType(val displayNameRes: Int) {
    Default(R.string.dispatchers_default),
    IO(R.string.dispatchers_io),
    Main(R.string.dispatchers_main)
}

class ToastException : Exception() {
    override val message: String = "Operation took too long! Showing Toast."
}

class SnackbarException : Exception() {
    override val message: String = "Operation timeout! Showing Snackbar."
}

class ResetException : Exception() {
    override val message: String = "Reset required due to long operation."
}

sealed class MainEvent {
    data class CoroutineCountChanged(val count: Float) : MainEvent()
    data class DispatcherChanged(val dispatcher: DispatcherType) : MainEvent()
    data class SequentialChanged(val isSequential: Boolean) : MainEvent()
    data class ParallelChanged(val isParallel: Boolean) : MainEvent()
    data class LazyChanged(val isLazy: Boolean) : MainEvent()
    object LaunchClicked : MainEvent()
    object CancelClicked : MainEvent()
}
