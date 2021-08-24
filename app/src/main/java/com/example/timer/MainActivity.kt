package com.example.timer

import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.timer.savedvar.SavedPrefs
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timer: Any
    private var secondsRemaining = 0L
    private var millisRemaining = 0L
    private var timeSeconds:Long = 20
    enum class TimerState{Stopped, Paused, Running}
    private var timerState = TimerState.Stopped

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateUI()
        start_stopButton.setOnClickListener {
            if(timerState==TimerState.Stopped){
                startTimer(timeSeconds * 1000)
                start_stopButton.setImageResource(R.drawable.ic_stop)
            }else{
                stopTimer()
                secondsRemaining = 0
                millisRemaining = 0
                updateUI()
                pause_playButton.setImageResource(R.drawable.ic_pause)
                start_stopButton.setImageResource(R.drawable.ic_start)
            }
        }
        pause_playButton.setOnClickListener {
            if(timerState == TimerState.Running){
                stopTimer()
                timerState = TimerState.Paused
                pause_playButton.setImageResource(R.drawable.ic_play)
            }else if(timerState == TimerState.Paused) {
                    startTimer(millisRemaining)
                    pause_playButton.setImageResource(R.drawable.ic_pause)
            }
        }
        replayButton.setOnClickListener {
            if(timerState!=TimerState.Stopped) {
                stopTimer()
                startTimer(timeSeconds * 1000)
                pause_playButton.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if(timerState==TimerState.Running){
            stopTimer()
            //Start background timer and show notification
        }else if(timerState==TimerState.Paused){
            //Show notification
        }

        SavedPrefs.setSecondsRemaining(this, secondsRemaining)
        SavedPrefs.setTimerState(this, timerState)

    }

    override fun onResume() {
        super.onResume()

        secondsRemaining = SavedPrefs.getSecondsRemaining(this)
        timerState = SavedPrefs.getTimerState(this)
        startTimer(secondsRemaining*1000)
    }
    private fun startTimer(timeInMillis: Long){
        timer = object: CountDownTimer(timeInMillis, 10){
            override fun onTick(millisUntilFinished: Long) {
                millisRemaining = millisUntilFinished
                secondsRemaining = millisRemaining/1000
                updateUI()
            }
            override fun onFinish() {
                Toast.makeText(this@MainActivity, "Countdown complete", Toast.LENGTH_SHORT).show()
                start_stopButton.setImageResource(R.drawable.ic_start)
                timerState = TimerState.Stopped
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
        val minutesRemaining = secondsRemaining/60
        val hoursRemaining = minutesRemaining/60
        val minutesRemainingStr = (minutesRemaining - hoursRemaining*60).toString()
        val secondsStr = (secondsRemaining - minutesRemainingStr.toInt()*60).toString()
        timerText.text = getString(R.string.uiTime,
                            if(hoursRemaining.toString().length == 2) hoursRemaining.toString()
                            else "0$hoursRemaining",
                            if(minutesRemainingStr.length == 2) minutesRemainingStr
                            else "0$minutesRemainingStr",
                            if(secondsStr.length == 2) secondsStr
                            else "0$secondsStr")
        progress_bar.setProgress((millisRemaining*100/(timeSeconds*1000)).toInt(),true)
    }
    private fun getNewTime(){
        timeSeconds = SavedPrefs.getNewTimerLength(this)
    }
}