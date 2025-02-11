package com.android.control

//librerie
import android.content.*
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Imposta il layout dell'interfaccia utente per l'attività utilizzando il file XML activity_login
        setContentView(R.layout.activity_login)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        //Se non esiste, restituisce null
        val savedPassword = sharedPreferences.getString("password_segreto", null)

        val etPassword = findViewById<EditText>(R.id.password_field)
        val btnLogin = findViewById<Button>(R.id.login_button)

        btnLogin.setOnClickListener {

            //Ottiene il testo inserito nell'EditText e lo converte in una stringa
            val enteredPassword = etPassword.text.toString()

            if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Inserisci una password", Toast.LENGTH_LONG).show()
                //per interrompere l'esecuzione della funzione.
                return@setOnClickListener
            }

            if (savedPassword == null) {
                // Salvo la password per la prima volta
                sharedPreferences.edit().putString("password_segreto", enteredPassword).apply() //"apply()" salva i dati in modo asincrono
                Toast.makeText(this, "password salvato", Toast.LENGTH_LONG).show()
                //Chiamo una funzione per aprire un'altra schermata dell'app.
                navigateToMainActivity()
            } else {
                // Controllo se la password è corretta
                if (enteredPassword == savedPassword) {
                    Toast.makeText(this, "correct password", Toast.LENGTH_LONG).show()
                    navigateToMainActivity()
                } else {
                    Toast.makeText(this, "password errata", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
       // Chiude l'attività corrente, rimuovendola dalla pila delle attività
        finish()
    }

}
