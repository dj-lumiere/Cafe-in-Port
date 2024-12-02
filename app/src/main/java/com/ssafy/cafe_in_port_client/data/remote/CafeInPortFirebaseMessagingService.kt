package com.ssafy.cafe_in_port_client.data.remote

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.cafe_in_port_client.R
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.data.model.dto.NotificationItem
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil.apiService
import com.ssafy.cafe_in_port_client.features.main.ui.MainActivity
import com.ssafy.cafe_in_port_client.features.notification.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CIPFbaseMessagingServ"

class CafeInPortFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_ID = "ssafy_channel"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        MainActivity.uploadToken(token)
        // TODO: Send the token to your server to register the device
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var messageTitle = ""
        var messageContent = ""

        if (remoteMessage.notification != null) { // notification이 있는 경우 foreground처리
            //foreground
            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()

        } else {  // background 에 있을경우 혹은 foreground에 있을경우 두 경우 모두
            val data = remoteMessage.data
            Log.d(TAG, "data.message: ${data}")
            Log.d(TAG, "data.message: ${data.get("myTitle")}")
            Log.d(TAG, "data.message: ${data.get("myBody")}")

            messageTitle = data.get("myTitle").toString()
            messageContent = data.get("myBody").toString()
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create NotificationItem
        val notificationItem = NotificationItem(
            R.drawable.cafe_logo, // Replace with actual drawable resource
            messageTitle,
            messageContent
        )

        // Add to repository
        NotificationRepository.addNotification(notificationItem)

        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder1 = NotificationCompat.Builder(this, MainActivity.channel_id)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(messageTitle)
            .setContentText(messageContent)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder1.build())
    }
}