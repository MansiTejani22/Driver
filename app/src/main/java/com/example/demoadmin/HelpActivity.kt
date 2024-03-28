package com.example.demoadmin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HelpActivity : AppCompatActivity() {
    private lateinit var imgCall: ImageView
    private lateinit var imgMessage: ImageView
    private lateinit var imgSos: ImageView
    private val REQUEST_SMS_PERMISSION = 123
    private val REQUEST_RECORD_AUDIO_PERMISSION = 456
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isRecording = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        imgCall = findViewById(R.id.ImgCall)
        imgMessage = findViewById(R.id.ImgMessage)
        imgSos = findViewById(R.id.ImgSos)

        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        // Check for RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }

        imgCall.setOnClickListener {
            val phoneNumber = "9723617158" // Replace with your phone number
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        }

        imgMessage.setOnClickListener {
            val phoneNumber = "9723617158" // Replace with the static phone number
            val message = "SOS: I need help!" // Static message indicating an SOS
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
            intent.putExtra("sms_body", message)
            startActivity(intent)
        }

        imgSos.setOnClickListener {
            Log.d("HelpActivity", "SOS button clicked")
            if (!isRecording) {
                // Start recording audio
                speechRecognizer.startListening(speechRecognizerIntent)
                isRecording = true
                // Show toast indicating recording has started
                Toast.makeText(applicationContext, "Recording SOS message...", Toast.LENGTH_SHORT).show()
                Log.d("HelpActivity", "Recording started")
            }
        }

        // Implement speech recognition listener
        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                isRecording = false
                Toast.makeText(applicationContext, "Error occurred during speech recognition", Toast.LENGTH_SHORT)
                    .show()
                Log.e("HelpActivity", "Error occurred during speech recognition: $error")
            }
            override fun onResults(results: Bundle?) {
                isRecording = false
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null && data.isNotEmpty()) {
                    val spokenText = data[0]
                    // Now you can send the spoken text via SMS
                    val phoneNumber = "7487849633" // Replace with the static phone number
                    val message = "SOS: $spokenText" // Include the spoken text in the SOS message
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
                    intent.putExtra("sms_body", message)
                    startActivity(intent)
                    Log.d("HelpActivity", "SOS message sent: $message")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the speech recognizer when the activity is destroyed
        speechRecognizer.destroy()
    }
}

