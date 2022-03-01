package xyz.daijoubuteam.SevenMinutesWorkout

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import xyz.daijoubuteam.SevenMinutesWorkout.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        setProgessBar()
    }

    private fun setupRestView() {
        if(restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        binding?.flProgressBarExercise?.visibility = View.INVISIBLE
        binding?.flProgressBar?.visibility = View.VISIBLE
        setProgessBar()
    }

    private fun setupExerciseView() {
        if(exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        binding?.flProgressBarExercise?.visibility = View.VISIBLE
        binding?.flProgressBar?.visibility = View.INVISIBLE
        setProgessBarExercise()
    }

    private fun setProgessBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                setupExerciseView()
            }
        }.start()
    }
    private fun setProgessBarExercise() {
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }
            override fun onFinish() {
                setupRestView()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        binding = null
    }
}