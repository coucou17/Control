package com.android.control

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private val smsPermissionCode = 101
    private lateinit var phoneNumberInput: EditText
    private lateinit var saveButton: Button
    private lateinit var sharedPreferences: SharedPreferences



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        checkAndRequestSMSPermission()

        phoneNumberInput = findViewById(R.id.inputAuthorizedNumber)
        saveButton = findViewById(R.id.saveButton)
        val error = findViewById<TextView>(R.id.error)


        saveButton.setOnClickListener {

            val phoneNumber = phoneNumberInput.text.toString()  //.replace(" ", "") // Rimuove tutti gli spazi
            sharedPreferences = getSharedPreferences("AuthorizedNumberPrefs", Context.MODE_PRIVATE)

            if (phoneNumber.isNotEmpty() ) {
                if (phoneNumber.contains(',') || phoneNumber.contains('.') || phoneNumber.contains('_') || phoneNumber.contains('-')
                    || phoneNumber.contains('/') || phoneNumber.contains(')') || phoneNumber.contains('(') || phoneNumber.contains(';')
                    || phoneNumber.contains('*') || phoneNumber.contains('N')|| phoneNumber.contains('#')) {
                    error.visibility = View.VISIBLE
                    error.text = getString(R.string.invalid_number)
                    error.clearComposingText()
                    Toast.makeText(this, "errore, numero non valido", Toast.LENGTH_SHORT).show()
                } else {
                sharedPreferences.edit().putString("authorized_number", phoneNumber).apply()
                // Cancello il campo dopo il salvataggio
                phoneNumberInput.text.clear()
                Toast.makeText(this, "numero $phoneNumber salvato", Toast.LENGTH_SHORT).show()
               }
            }
              else
                    {
                    error.visibility = View.VISIBLE
                    error.text = getString(R.string.invalid_number)
                    error.clearComposingText()
                    Toast.makeText(this, "errore, numero non valido", Toast.LENGTH_LONG).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS, Manifest.permission.CAMERA),
            smsPermissionCode
        )

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestSMSPermission() {
        when {
            ( (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_SMS
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) &&
                    ( ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED) ) -> {
                // L'autorizzazione è già stata concessa
                Toast.makeText(this, "Autorizzazioni SMS già impostati", Toast.LENGTH_SHORT).show()
            }

            else -> {
                // Chiedo L'autorizzazione
                requestSmsPermission()
            }


        }


        }

}


