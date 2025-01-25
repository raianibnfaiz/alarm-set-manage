package com.bjit.alarmmanager

data class Alarm(
    val id: Int,              // Unique ID for the alarm
    val timeInMillis: Long,   // Alarm time
    val customAudioUri: String? = null // Optional custom audio
)