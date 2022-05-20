package ru.kirill.weather_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.kirill.weather_app.view.MainActivity

class MyService: FirebaseMessagingService() {

    companion object{
        private const val NOTIFICATION_ID_HIGH = 1
        private const val CHANNEL_ID_HIGH = "channel_id_1"
        private const val KEY_TITLE = "myTitle"
        private const val KEY_MESSAGE = "myMessage"
    }


    override fun onMessageReceived(message: RemoteMessage) {
        if(!message.data.isNullOrEmpty()){
            val title = message.data[KEY_TITLE]
            val message_text = message.data[KEY_MESSAGE]
            if(!title.isNullOrEmpty()&&!message_text.isNullOrEmpty()){
                push(title, message_text)
            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("@@@", "$token")
    }

    private fun push(title:String,message:String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(applicationContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilderHigh= NotificationCompat.Builder(this, CHANNEL_ID_HIGH).apply {
            setSmallIcon(R.drawable.ic_baseline_send_24)
            setContentTitle(title)
            setContentText(message)
            setContentIntent(contentIntent)
            priority = NotificationManager.IMPORTANCE_HIGH
        }

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channelNameLow = "Name $CHANNEL_ID_HIGH"
            val channelDescriptionLow = "Description $CHANNEL_ID_HIGH"
            val channelPriorityLow = NotificationManager.IMPORTANCE_HIGH
            val channelLow = NotificationChannel(CHANNEL_ID_HIGH,channelNameLow,channelPriorityLow).apply {
                description = channelDescriptionLow
            }
            notificationManager.createNotificationChannel(channelLow)
        }

        notificationManager.notify(NOTIFICATION_ID_HIGH,notificationBuilderHigh.build())
    }

}