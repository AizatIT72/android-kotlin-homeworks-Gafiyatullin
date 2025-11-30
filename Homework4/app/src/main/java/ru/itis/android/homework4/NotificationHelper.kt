package ru.itis.android.homework4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        id: Int,
        data: NotificationData,
        replyAction: (() -> Unit)? = null
    ) {
        val notification = buildNotification(id, data, replyAction)
        notificationManager.notify(id, notification)
    }

    fun updateNotification(id: Int, newMessage: String): Boolean {
        val activeNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications
        } else {
            emptyArray()
        }

        val notificationExists = activeNotifications.any { it.id == id }

        if (notificationExists) {
            val updatedNotification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.updated_notification_title))
                .setContentText(newMessage)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify(id, updatedNotification)
            return true
        }
        return false
    }

    fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }

    fun hasActiveNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications.isNotEmpty()
        } else {
            true
        }
    }

    private fun buildNotification(
        id: Int,
        data: NotificationData,
        replyAction: (() -> Unit)? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setContentTitle(data.title)
            .setContentText(data.message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(getPriority(data.priority))
            .setAutoCancel(true)

        if (data.shouldExpand && data.message.isNotEmpty()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(data.message))
        }

        if (data.openOnClick) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(context.getString(R.string.intent_extra_title), data.title)
                putExtra(context.getString(R.string.intent_extra_message), data.message)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }

        if (data.hasReplyAction && replyAction != null) {
            val remoteInput = RemoteInput.Builder(Constants.KEY_REPLY).run {
                setLabel(context.getString(R.string.reply_hint))
                build()
            }

            val replyIntent = Intent(context, MainActivity::class.java).apply {
                action = Constants.REPLY_ACTION
                putExtra(Constants.NOTIFICATION_ID_EXTRA, id)
            }

            val replyPendingIntent = PendingIntent.getActivity(
                context,
                id,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val action = NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                context.getString(R.string.reply),
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
        }

        return builder.build()
    }

    private fun getPriority(priority: NotificationPriority): Int {
        return when (priority) {
            NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
            NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationPriority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        }
    }
}