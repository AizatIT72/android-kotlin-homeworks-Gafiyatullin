package ru.itis.android.homework4

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainViewModel : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> get() = _messages

    fun addMessage(message: String) {
        if (message.isNotBlank()) {
            _messages.add("${getCurrentTime()}: $message")
        }
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault())
            .format(Date())
    }
}