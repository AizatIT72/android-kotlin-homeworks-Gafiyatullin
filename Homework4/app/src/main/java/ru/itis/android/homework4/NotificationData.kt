package ru.itis.android.homework4

import androidx.core.app.NotificationCompat

data class NotificationData(
    val title: String = "",
    val message: String = "",
    val shouldExpand: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val openOnClick: Boolean = false,
    val hasReplyAction: Boolean = false
)

enum class NotificationPriority(val importance: Int) {
    MIN(NotificationCompat.PRIORITY_MIN),
    LOW(NotificationCompat.PRIORITY_LOW),
    MEDIUM(NotificationCompat.PRIORITY_DEFAULT),
    HIGH(NotificationCompat.PRIORITY_HIGH)
}