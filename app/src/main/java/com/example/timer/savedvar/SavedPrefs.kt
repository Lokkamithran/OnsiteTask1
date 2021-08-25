package com.example.timer.savedvar

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.timer.MainActivity

class SavedPrefs {
    companion object{

        fun setNewTimerLength(context: Context, hours: Long, minutes: Long, seconds: Long) {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("newTimerHours", hours)
            editor.putLong("newTimerMinutes",minutes)
            editor.putLong("newTimerSeconds", seconds)
            editor.apply()
        }
        fun setIsTimeDisplayed(context: Context, isTimeDisplayed: Boolean){
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isTimeDisplayed", isTimeDisplayed)
            editor.apply()
        }
        fun getIsTimeDisplayed(context: Context): Boolean{
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            return sharedPreferences.getBoolean("isTimeDisplayed", false)
        }
        fun setPreviousTimerSeconds(context: Context, seconds: Long) {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("previousTimerSeconds", seconds)
            editor.apply()
        }

        fun getPreviousTimerSeconds(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            return sharedPreferences.getLong("previousTimerSeconds", 0)
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

        fun setAlarmSetTime(context: Context, time: Long) {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("alarmSetTime", time)
            editor.apply()
        }

        fun getAlarmSetTime(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            return sharedPreferences.getLong("alarmSetTime",0)
        }

    }
}