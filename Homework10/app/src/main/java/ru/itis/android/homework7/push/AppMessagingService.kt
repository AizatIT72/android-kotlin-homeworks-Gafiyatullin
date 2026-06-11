package ru.itis.android.homework7.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.itis.android.homework7.MainActivity
import ru.itis.android.homework7.R

class AppMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val kind = data[KEY_KIND].orEmpty()
        val title = data[KEY_TITLE].orEmpty()
        val body = data[KEY_MESSAGE].orEmpty()

        when (kind) {
            KIND_PROMO -> showPromoNotification(title, body)
            KIND_AUTH -> showAuthNotification(title, body)
            KIND_INFO -> showInfoNotification(title, body)
            else -> showInfoNotification(
                title.ifBlank { getString(R.string.notif_default_title) },
                body.ifBlank { getString(R.string.notif_default_body) },
            )
        }
    }

    private fun showPromoNotification(title: String, body: String) {
        ensureChannel(CHANNEL_PROMO, R.string.channel_promo, NotificationManager.IMPORTANCE_HIGH)
        val notif = baseBuilder(CHANNEL_PROMO)
            .setContentTitle(title.ifBlank { getString(R.string.notif_promo_default_title) })
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_PROMO)
            .build()
        notify(NOTIF_ID_PROMO, notif)
    }

    private fun showAuthNotification(title: String, body: String) {
        ensureChannel(CHANNEL_AUTH, R.string.channel_auth, NotificationManager.IMPORTANCE_HIGH)
        val notif = baseBuilder(CHANNEL_AUTH)
            .setContentTitle(title.ifBlank { getString(R.string.notif_auth_default_title) })
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        notify(NOTIF_ID_AUTH, notif)
    }

    private fun showInfoNotification(title: String, body: String) {
        ensureChannel(CHANNEL_INFO, R.string.channel_info, NotificationManager.IMPORTANCE_DEFAULT)
        val notif = baseBuilder(CHANNEL_INFO)
            .setContentTitle(title.ifBlank { getString(R.string.notif_default_title) })
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        notify(NOTIF_ID_INFO, notif)
    }

    private fun baseBuilder(channelId: String): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setContentIntent(pi)
    }

    private fun ensureChannel(id: String, nameRes: Int, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(id) == null) {
                nm.createNotificationChannel(NotificationChannel(id, getString(nameRes), importance))
            }
        }
    }

    private fun notify(id: Int, notification: android.app.Notification) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(id, notification)
    }

    companion object {
        private const val KEY_KIND = "kind"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"

        private const val KIND_PROMO = "promo"
        private const val KIND_AUTH = "auth"
        private const val KIND_INFO = "info"

        private const val CHANNEL_PROMO = "channel_promo"
        private const val CHANNEL_AUTH = "channel_auth"
        private const val CHANNEL_INFO = "channel_info"

        private const val NOTIF_ID_PROMO = 1001
        private const val NOTIF_ID_AUTH = 1002
        private const val NOTIF_ID_INFO = 1003
    }
}