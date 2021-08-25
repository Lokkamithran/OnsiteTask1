package com.example.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.timer.savedvar.SavedPrefs

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            AppConstants.ACTION_STOP -> {
                MainActivity.cancelAlarm(context)
                SavedPrefs.setTimerState(context, MainActivity.TimerState.Stopped)
                Notification.hideTimerNotification(context)
            }
            AppConstants.ACTION_PLAY -> {
                val secondsRemaining = SavedPrefs.getSecondsRemaining(context)

                val wakeupTime = MainActivity.setAlarm(context, MainActivity.nowSeconds, secondsRemaining)
                SavedPrefs.setTimerState(context, MainActivity.TimerState.Running)
                Notification.showTimerRunning(context, wakeupTime)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = SavedPrefs.getSecondsRemaining(context)
                val alarmSetTime = SavedPrefs.getAlarmSetTime(context)
                val nowSeconds = MainActivity.nowSeconds

                secondsRemaining -= (nowSeconds - alarmSetTime)
                SavedPrefs.setSecondsRemaining(context, secondsRemaining)

                MainActivity.cancelAlarm(context)
                SavedPrefs.setTimerState(context, MainActivity.TimerState.Paused)
                Notification.showTimerPaused(context)
            }
            AppConstants.ACTION_START -> {
                val secondsRemaining = SavedPrefs.getNewTimerLength(context)
                val wakeupTime = MainActivity.setAlarm(context, MainActivity.nowSeconds, secondsRemaining)

                SavedPrefs.setTimerState(context, MainActivity.TimerState.Running)
                SavedPrefs.setSecondsRemaining(context, secondsRemaining)
                Notification.showTimerRunning(context, wakeupTime)
            }
        }
    }
}