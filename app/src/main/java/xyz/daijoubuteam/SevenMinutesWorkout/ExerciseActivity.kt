package xyz.daijoubuteam.SevenMinutesWorkout

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.daijoubuteam.SevenMinutesWorkout.databinding.ActivityExerciseBinding
import xyz.daijoubuteam.SevenMinutesWorkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Int = 1

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Int = 1

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition: Int = -1

    private var tts: TextToSpeech? = null

    private var exerciseStatusAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }
        binding?.tvUpcomingName?.text =
            exerciseList!![currentExercisePosition + 1].getName()
        setupExerciseStatusAdapter()
        setProgressBar()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton() {
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            dialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupExerciseStatusAdapter() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseStatusAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseStatusAdapter
    }

    private fun setupRestView() {
        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flProgressBarExercise?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingName?.visibility = View.VISIBLE
        speakOut("Rest now")
        setProgressBar()
    }

    private fun setupExerciseView() {
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flProgressBarExercise?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingName?.visibility = View.INVISIBLE
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        speakOut(exerciseList!![currentExercisePosition].getName())
        setProgressBarExercise()
    }

    private fun setProgressBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration.toLong() * 1000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseStatusAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    private fun setProgressBarExercise() {
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration.toLong() * 1000, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration - exerciseProgress
                binding?.tvTimerExercise?.text =
                    (exerciseTimerDuration - exerciseProgress).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseStatusAdapter!!.notifyDataSetChanged()
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    if (currentExercisePosition < exerciseList?.size!! - 2) {
                        binding?.tvUpcomingName?.text =
                            exerciseList!![currentExercisePosition + 1].getName()
                    }
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified not supported")
            }

        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}