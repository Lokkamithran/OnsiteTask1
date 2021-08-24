package com.example.timer.savedvar

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.timer.MainActivity

class SavedPrefs {
    companion object{

        fun getNewTimerLength(context: Context): Long{
            //placeHolder fun
            return 1
        }

        fun setSecondsRemaining(context: Context, millis: Long) {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("secondsRemaining", millis)
            editor.apply()
        }

        fun getSecondsRemaining(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            return sharedPreferences.getLong("secondsRemaining", 0)
        }

        fun setTimerState(context: Context, state: MainActivity.TimerState) {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("timerState", state.ordinal)
            editor.apply()
        }

        fun getTimerState(context: Context): MainActivity.TimerState {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            return MainActivity.TimerState.values()[sharedPreferences.getInt("timerState", 0)]
        }
    }
}