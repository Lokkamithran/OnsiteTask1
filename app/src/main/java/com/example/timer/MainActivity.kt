package com.example.timer

import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timer: Any
    private var timeRunning = false
    private var millisRemaining: Long = 0
    private var time:Long = 20
    private var startButtonFlag = true

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText.text = getString(R.string.emptyTime)
        progress_bar.progress = 0
        start_stopButton.setOnClickListener {
            startButtonFlag = if(startButtonFlag){
                startTimer(time * 1000)
                start_stopButton.setImageResource(R.drawable.ic_stop)
                false
            }else{
                stopTimer()
                timerText.text = getString(R.string.emptyTime)
                progress_bar.progress = 0
                start_stopButton.setImageResource(R.drawable.ic_start)
                true
            }
        }
        pause_playButton.setOnClickListener {
            if(timeRunning){
                stopTimer()
                pause_playButton.setImageResource(R.drawable.ic_play)
            }else {
                startTimer(millisRemaining)
                pause_playButton.setImageResource(R.drawable.ic_pause)
            }
        }
        replayButton.setOnClickListener {
            if(timeRunning){ startTimer(time * 1000) }
        }
    }
    private fun startTimer(timeInMillis: Long){
        timer = object: CountDownTimer(timeInMillis, 1000){
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTick(millisUntilFinished: Long) {
                millisRemaining = millisUntilFinished
                timerText.text = (millisUntilFinished/1000).toString()
                progress_bar.setProgress((millisUntilFinished*100/(time*1000)).toInt(),true)
            }
            override fun onFinish() {
                Toast.makeText(this@MainActivity, "Countdown complete", Toast.LENGTH_SHORT).show()
                start_stopButton.setImageResource(R.drawable.ic_start)
                startButtonFlag = true
            }
        }
        (timer as CountDownTimer).start()
        timeRunning = true
    }
    private fun stopTimer(){
        (timer as CountDownTimer).cancel()
        timeRunning = false
    }
}