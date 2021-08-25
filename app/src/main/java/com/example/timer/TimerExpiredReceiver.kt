package com.example.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timer.savedvar.SavedPrefs

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: Show notification

        SavedPrefs.setAlarmSetTime(context, 0)
        SavedPrefs.setTimerState(context, MainActivity.TimerState.Stopped)
    }
}