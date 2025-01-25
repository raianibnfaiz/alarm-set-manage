package com.bjit.alarmmanager


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class AddAlarmBottomSheet(
    private val onSaveAlarm: (Calendar) -> Unit // Callback to save alarm data
) : BottomSheetDialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set the custom BottomSheet layout
        val contentView = layoutInflater.inflate(R.layout.layout_bottom_sheet_add_alarm, null)
        dialog.setContentView(contentView)

        // TimePicker from BottomSheet layout
        val timePicker = contentView.findViewById<TimePicker>(R.id.timePicker)
        val btnSaveAlarm = contentView.findViewById<Button>(R.id.btnSaveAlarm)

        // Handle Save Button Click
        btnSaveAlarm.setOnClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            // Create a Calendar instance for the selected time
            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }

            // Use the callback function to save the alarm
            onSaveAlarm(alarmTime)

            dismiss() // Close the BottomSheet after saving the alarm
        }

        return dialog
    }
}
