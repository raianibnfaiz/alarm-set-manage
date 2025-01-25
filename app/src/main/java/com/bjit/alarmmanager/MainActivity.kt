package com.bjit.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

import java.util.*

class MainActivity : AppCompatActivity() {

    private val alarms = mutableListOf<Alarm>() // List of alarms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Handle Floating Action Button Click
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            openAddAlarmBottomSheet()
        }

        // Load saved alarms (if any)
        loadAlarms()
    }

    // Function to open the BottomSheet for adding alarms
    private fun openAddAlarmBottomSheet() {
        val addAlarmBottomSheet = AddAlarmBottomSheet { alarmTime ->
            // Callback when user selects alarm time and clicks "Save"
            createAndSaveAlarm(alarmTime.timeInMillis)
        }
        addAlarmBottomSheet.show(supportFragmentManager, "AddAlarmBottomSheet")
    }

    // Create a new alarm and save it
    private fun createAndSaveAlarm(timeInMillis: Long) {
        val alarmId = System.currentTimeMillis().toInt() // Unique ID
        val alarm = Alarm(alarmId, timeInMillis)

        setAlarm(alarm) // Schedule the alarm
        alarms.add(alarm)


        saveAlarms(alarms) // Persist alarms
    }

    // Cancel an alarm
    private fun cancelAlarm(alarmId: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, alarmId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        alarms.removeIf { it.id == alarmId }
        saveAlarms(alarms)

        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }

    // Set an alarm using AlarmManager
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(alarm: Alarm) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)
        intent.putExtra("ALARM_ID", alarm.id)

        val pendingIntent = PendingIntent.getBroadcast(
            this, alarm.id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
    }

    // Save alarms to SharedPreferences
    private fun saveAlarms(alarms: List<Alarm>) {
        val sharedPreferences = getSharedPreferences("AlarmAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.apply()
    }

    // Load alarms from SharedPreferences
    private fun loadAlarms() {
        val sharedPreferences = getSharedPreferences("AlarmAppPrefs", MODE_PRIVATE)
        val json = sharedPreferences.getString("alarms", null) ?: return
        val type = object : TypeToken<List<Alarm>>() {}.type

        alarms.clear()

    }
}