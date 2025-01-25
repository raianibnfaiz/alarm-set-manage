package com.bjit.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyAlarm : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyAlarm", "Alarm triggered!")

        // Vibrate the device
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val audioUriString = intent.getStringExtra("ALARM_AUDIO_URI")
        val audioUri = if (audioUriString != null) Uri.parse(audioUriString) else null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            vibrator.vibrate(10000) // Vibrate for 1 second
        }

        // Play alarm sound
        try {
            // Create the MediaPlayer

            if (audioUri != null) {
                // Play custom alarm tone if URI is valid
                mediaPlayer?.setDataSource(context, audioUri)
                Toast.makeText(context, "Playing Custom Alarm Tone!", Toast.LENGTH_SHORT).show()
            } else {
                // Fallback to the default alarm tone if no custom alarm was selected
                mediaPlayer?.setDataSource(
                    context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                )
                Toast.makeText(context, "Playing Default Alarm Tone!", Toast.LENGTH_SHORT).show()
            }

            // Prepare and play the tone
            mediaPlayer?.apply {
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to play alarm sound!", Toast.LENGTH_SHORT).show()
        }

        // Show notification
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val channelId = "alarm_notification_channel"
        val channelName = "Alarm Notifications"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm) // Use your own icon
            .setContentTitle("Alarm Triggered")
            .setContentText("Your alarm has gone off.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        notificationManager.notify(1, builder.build())
    }
}