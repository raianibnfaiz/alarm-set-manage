package com.bjit.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var btnSetAlarm: Button
    lateinit var btnSelectAudio: Button
    lateinit var timePicker: TimePicker

    private val PICK_AUDIO_REQUEST_CODE = 1234
    private var selectedAudioUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Alarm App"

        timePicker = findViewById(R.id.timePicker)
        btnSetAlarm = findViewById(R.id.buttonAlarm)
        btnSelectAudio = findViewById(R.id.btnSelectAudio)

        btnSelectAudio.setOnClickListener {
            pickCustomAudio()
        }

        btnSetAlarm.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            if (Build.VERSION.SDK_INT >= 23) {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour,
                    timePicker.minute,
                    0
                )
            } else {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.currentHour,
                    timePicker.currentMinute,
                    0
                )
            }
            setAlarm(calendar.timeInMillis)
        }
    }

    private fun pickCustomAudio() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.type = "audio/*"
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected audio URI
            selectedAudioUri = data.data
            saveAudioUri(selectedAudioUri)
            Toast.makeText(this, "Custom Alarm Tone Selected!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAudioUri(uri: Uri?) {
        val sharedPreferences = getSharedPreferences("AlarmAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (uri != null) {
            editor.putString("CUSTOM_ALARM_URI", uri.toString())  // Save valid custom URI
        } else {
            editor.remove("CUSTOM_ALARM_URI")  // Remove any previously saved URI, reset to default
        }
        editor.apply()
    }

    private fun getSavedAudioUri(): Uri? {
        val sharedPreferences = getSharedPreferences("AlarmAppPrefs", Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString("CUSTOM_ALARM_URI", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)

        // Pass the saved custom alarm tone URI (or none)
        val customAudioUri = getSavedAudioUri()
        if (customAudioUri != null) {
            // Custom alarm tone selected
            intent.putExtra("ALARM_AUDIO_URI", customAudioUri.toString())
        } else {
            // No custom alarm tone, fallback to default
            intent.putExtra("ALARM_AUDIO_URI", R.raw.default_alarm)  // Pass empty string to indicate no custom tone
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Toast.makeText(this, "Alarm is set!", Toast.LENGTH_SHORT).show()
    }
}