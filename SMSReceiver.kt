package com.android.control


import android.location.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.content.*
import android.telephony.*
import android.media.*
import android.annotation.SuppressLint
import android.os.*
import android.net.Uri
import android.util.Log


class SMSReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null


    @SuppressLint("SuspiciousIndentation", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
        val extras = intent.extras
        // Recupera il numero autorizzato dalle SharedPreferences
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AuthorizedNumberPrefs", Context.MODE_PRIVATE)
        val authorizedNumber = sharedPreferences.getString("authorized_number", "")

        // Se il numero non è autorizzato, viene visualizzato un messaggio e si interrompe l'esecuzione
        if (authorizedNumber.isNullOrEmpty()) {
            Toast.makeText(context, "NESSUN NUMERO AUTORIZZATO", Toast.LENGTH_LONG).show()
            return
        }

        if (extras != null) {

            val pdus = extras.get("pdus") as? Array<*>

            if (pdus != null) {
                val messages: Array<SmsMessage?> = arrayOfNulls(pdus.size)

                for (i in pdus.indices) {
                    messages[i] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val format = extras.getString("format")
                        SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                    } else {
                        SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                }

                val senderNumber = messages[0]?.originatingAddress
                val messageBody = messages[0]?.messageBody


                if (senderNumber == authorizedNumber) {
                    executeCommand(messageBody ?: "", context)
                } else {
                    Toast.makeText(
                        context,
                        "Numero non autorizzato: $senderNumber",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } }
        }
    }

    // eseguire i comandi in funzione del contenuto dell' SMS
    private fun executeCommand(messageBody: String, context: Context) {

        when {
            messageBody.contains("INSTAGRAM", ignoreCase = true) -> {
                openInstagram(context)
            }

            messageBody.contains("play sound", ignoreCase = true) -> {
                playSound(context)
            }

            messageBody.contains("location", ignoreCase = true) -> {
                getLocationAndSendSms(context)
            }

            messageBody.contains("fotoCamera", ignoreCase = true) -> {
                openCamera(context)
            }

            messageBody.contains("UNIPG", ignoreCase = true) -> {
                openWebsite(context)
                Toast.makeText(context, "Site Unipg aperto", Toast.LENGTH_LONG).show()
            }

            else -> Toast.makeText(context, "COMMANDO SCONOSCIUTO", Toast.LENGTH_LONG).show()
        }
    }


    // aprire un site web
    private fun openWebsite(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.unipg.it"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    // Ouvre une application
    private fun openInstagram(context: Context) {
        val packageName = "com.instagram.android"
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            Toast.makeText(context, "Instagram è aperto", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Instagram non è installato", Toast.LENGTH_LONG).show()
        }
    }


    private fun playSound(context: Context) {
        // Vérifie les permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si les permissions sont manquantes, retourne null
            Toast.makeText(context, "problema con i permessi", Toast.LENGTH_LONG).show()
        } else {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.sound)
            }
            mediaPlayer?.start()
            Toast.makeText(context, "La musica inizia", Toast.LENGTH_LONG).show()
        }
    }




    private fun getLocation(context: Context): Location? {
        // Accéder au LocationManager
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Vérifie les permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si les permissions sont manquantes, retourne null
            Toast.makeText(context, "problema con i permessi", Toast.LENGTH_LONG).show()
            return null
        }

        // Essaie de récupérer la dernière localisation connue
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        // Exemple de log pour déboguer
        Log.d("LocationDebug", "Latitude: ${location?.latitude}, Longitude: ${location?.longitude}")

        if (location == null) {
            Toast.makeText(context, "La localisation n'est pas disponible", Toast.LENGTH_LONG).show()
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // Intervalle en millisecondes
                1f     // Distance minimale en mètres
            ) { updatedLocation ->
                Log.d("LocationUpdate", "Nouvelle localisation : ${updatedLocation.latitude}, ${updatedLocation.longitude}")
            }
        } else {
            Log.d(
                "LocationDebug",
                "Localisation obtenue: Latitude = ${location.latitude}, Longitude = ${location.longitude}"
            )
        }
        return location
    }




    private fun getLocationAndSendSms(context: Context) {

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AuthorizedNumberPrefs", Context.MODE_PRIVATE)
        val authorizedNumber = sharedPreferences.getString("authorized_number", "")

        // Obtenir la localisation de l'appareil
        val location = getLocation(context)

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            // Format de la localisation
            val locationMessage = "Ma localisation actuelle: Lat: $latitude, Long: $longitude"

            // Envoyer la localisation par SMS
            sendSms(authorizedNumber, locationMessage)
        } else {
            sendSms(authorizedNumber, "Impossible d'obtenir la localisation.")
        }
    }

    private fun sendSms(authorizedNumber: String?, message: String) {
        Log.d("LocationDebug", "Envoi du SMS : $message")
        if (authorizedNumber != null) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(authorizedNumber, null, message, null, null)
        }
    }


    // Funzione per aprire la fotocamera
    private fun openCamera(context: Context) {
        // Vérifie les permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "problema con i permessi", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(context, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }
}
