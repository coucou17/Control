package com.android.control

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent per aprire la fotocamera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //verifico se il dispositivo ha un'app fotocamera installata
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivity(cameraIntent)
            Toast.makeText(this, "fotoCamera aperta", Toast.LENGTH_LONG).show()
        } else {
            // Mostra un messaggio se nessuna app può gestire l'intent
            Toast.makeText(this, "Nessuna app fotocamera trovata", Toast.LENGTH_LONG).show()
        }

        // Chiudi l'attività dopo aver avviato la fotocamera
        finish()
    }
}
