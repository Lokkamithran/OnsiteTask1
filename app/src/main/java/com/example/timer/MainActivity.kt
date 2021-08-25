package com.example.timer

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.timer.savedvar.SavedPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog_box.*
import java.util.*
import android.util.DisplayMetrics




class MainActivity : AppCompatActivity() {

    companion object{
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis/1000
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeupTime = (nowSeconds+secondsRemaining)*1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeupTime, pendingIntent)
            SavedPrefs.setAlarmSetTime(context, nowSeconds)
            return wakeupTime
        }
        fun cancelAlarm(context: Context){
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.cancel(pendingIntent)
            SavedPrefs.setAlarmSetTime(context, 0)
        }
        var timeStr: String = "00:00:00"
    }
    private lateinit var timer: Any
    private var secondsRemaining = 0L
    private var millisRemaining = 0L
    private var timeSeconds: Long = 0
    enum class TimerState{Stopped, Paused, Running}
    private var timerState = TimerState.Stopped
    private var isTimeSet = false

    private var width: Int = 0
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val metrics: DisplayMetrics = resources.displayMetrics
        width = metrics.widthPixels
        height = metrics.heightPixels

        updateUI()
        start_stopButton.setOnClickListener {
            if(timerState==TimerState.Stopped){
                isTimeSet = false
                setNewTimerLength()
                if(timeSeconds == 0L) Toast.makeText(this, "Set time first:)", Toast.LENGTH_SHORT).show()
                else{
                    startTimer(timeSeconds * 1000)
                    updateButtons()
                }
            }else{
                stopTimer()
                secondsRemaining = 0
                millisRemaining = 0
                SavedPrefs.setNewTimerLength(this, 0, 0, 0)
                updateUI()
                updateButtons()
            }
        }
        pause_playButton.setOnClickListener {
            if(timerState == TimerState.Running){
                stopTimer()
                timerState = TimerState.Paused
                updateButtons()
            }else if(timerState == TimerState.Paused) {
                    startTimer(millisRemaining)
                    updateButtons()
            }
        }
        replayButton.setOnClickListener {
            if(timerState!=TimerState.Stopped) {
                stopTimer()
                startTimer(timeSeconds * 1000)
                updateButtons()
            }
        }
        setTimeFAB.setOnClickListener{
            showDialog()
            setNewTimerLength()
            isTimeSet = true
            millisRemaining = 0
            updateUI()
        }
    }

    override fun onPause() {
        super.onPause()

        SavedPrefs.setPreviousTimerSeconds(this, timeSeconds)
        SavedPrefs.setIsTimeDisplayed(this, isTimeSet)
        SavedPrefs.setSecondsRemaining(this, secondsRemaining)
        SavedPrefs.setTimerState(this, timerState)

        if(timerState==TimerState.Running){
            stopTimer()
            val wakeupTime = setAlarm(this, nowSeconds, secondsRemaining)
            Notification.showTimerRunning(this, wakeupTime)
        }else if(timerState==TimerState.Paused){
            Notification.showTimerPaused(this)
        }
    }

    override fun onResume() {
        super.onResume()
        initTimer()

        cancelAlarm(this)
        Notification.hideTimerNotification(this)
    }
    private fun initTimer(){
        timerState = SavedPrefs.getTimerState(this)
        timeSeconds = SavedPrefs.getPreviousTimerSeconds(this)

        secondsRemaining = if(timerState==TimerState.Paused || timerState==TimerState.Running)
            SavedPrefs.getSecondsRemaining(this)
        else
            0

        val alarmSetTime = SavedPrefs.getAlarmSetTime(this)
        if(alarmSetTime>0)
            secondsRemaining -= (nowSeconds-alarmSetTime)

        if(secondsRemaining<=0)
            onTimerFinished()
        else if(timerState==TimerState.Running)
            startTimer(secondsRemaining*1000)
        updateUI()
        updateButtons()
        millisRemaining = secondsRemaining*1000
        isTimeSet = SavedPrefs.getIsTimeDisplayed(this)
        if(isTimeSet) {
            showAsIs(SavedPrefs.getNewTimerLength(this))
        }
    }
    private fun setNewTimerLength(){
        timeSeconds = SavedPrefs.getNewTimerLength(this)
    }

    private fun startTimer(timeInMillis: Long){
        timer = object: CountDownTimer(timeInMillis, 10){
            override fun onTick(millisUntilFinished: Long) {
                millisRemaining = millisUntilFinished
                secondsRemaining = millisRemaining/1000
                updateUI()
            }
            override fun onFinish() {
                onTimerFinished()
            }
        }
        (timer as CountDownTimer).start()
        timerState = TimerState.Running
    }
    private fun stopTimer(){
        (timer as CountDownTimer).cancel()
        timerState = TimerState.Stopped
    }

    private fun updateUI(){
        if(timeSeconds==0L || secondsRemaining == 0L) {
            timerText.text = getString(R.string.uiTime, "00", "00", "00")
            timeStr = timerText.text.toString()
            progress_bar.setProgress(0,true)
        }else {
            val minutesRemaining = secondsRemaining / 60
            val hoursRemaining = minutesRemaining / 60
            val minutesRemainingStr = (minutesRemaining - hoursRemaining * 60).toString()
            val secondsStr = (secondsRemaining - minutesRemainingStr.toInt()*60 -hoursRemaining*60*60).toString()
            timerText.text = getString(
                R.string.uiTime,
                if (hoursRemaining.toString().length == 2) hoursRemaining.toString()
                else "0$hoursRemaining",
                if (minutesRemainingStr.length == 2) minutesRemainingStr
                else "0$minutesRemainingStr",
                if (secondsStr.length == 2) secondsStr
                else "0$secondsStr"
            )
            timeStr = timerText.text.toString()
            progress_bar.setProgress((secondsRemaining * 100 / (timeSeconds)).toInt(), true)
        }
    }
    private fun updateButtons(){
        when(timerState){
            TimerState.Running -> {
                pause_playButton.setImageResource(R.drawable.ic_pause)
                start_stopButton.setImageResource(R.drawable.ic_stop)
            }
            TimerState.Paused -> {
                pause_playButton.setImageResource(R.drawable.ic_play)
                start_stopButton.setImageResource(R.drawable.ic_stop)
            }
            TimerState.Stopped -> {
                pause_playButton.setImageResource(R.drawable.ic_pause)
                start_stopButton.setImageResource(R.drawable.ic_start)
            }
        }
    }
    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog_box)

        val getTimeButton = dialog.findViewById<Button>(R.id.getTimeButton)
        val hourInput = dialog.findViewById<TextView>(R.id.hourInput)
        val minuteInput = dialog.findViewById<TextView>(R.id.minuteInput)
        val secondInput = dialog.findViewById<TextView>(R.id.secondInput)
        getTimeButton.setOnClickListener {
            val hour = if(hourInput.text.toString()=="") 0
                    else hourInput.text.toString().toLong()
            val minute = if(minuteInput.text.toString()=="") 0
                    else minuteInput.text.toString().toLong()
            val second = if(secondInput.text.toString()=="") 0
                    else secondInput.text.toString().toLong()
            Log.d("mainActivity","Test: $hour, $minute, $second")
            if(hour>99 || minute>60 || second>60)
                Toast.makeText(this, "Input invalid or too large",Toast.LENGTH_SHORT).show()
            else {
                SavedPrefs.setNewTimerLength(this, hour, minute, second)
                showAsIs(SavedPrefs.getNewTimerLength(this))
            }
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(5*width/6,height/2)
    }
    private fun showAsIs(second: Long){
        val minutesRemaining = second / 60
        val hoursRemaining = minutesRemaining / 60
        val minutesRemainingStr = (minutesRemaining - hoursRemaining * 60).toString()
        val secondsStr = (second - minutesRemainingStr.toInt()*60 -hoursRemaining*60*60).toString()
        timerText.text = getString(
            R.string.uiTime,
            if (hoursRemaining.toString().length == 2) hoursRemaining.toString()
            else "0$hoursRemaining",
            if (minutesRemainingStr.length == 2) minutesRemainingStr
            else "0$minutesRemainingStr",
            if (secondsStr.length == 2) secondsStr
            else "0$secondsStr"
        )
        timeStr = timerText.text.toString()
        if(second!=0L)
            progress_bar.setProgress(100, true)
        else
            progress_bar.setProgress(0,true)
    }
    private fun onTimerFinished(){
        Toast.makeText(this@MainActivity, "Countdown complete", Toast.LENGTH_SHORT).show()
        updateButtons()
        timerState = TimerState.Stopped
    }
}