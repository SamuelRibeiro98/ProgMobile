package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.data.AuthResponseData
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.storage.TokenStorage
import com.samuelribeiro.polyhome.data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        autoLogin()
    }

    fun registerNewAccount(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        val loginField = findViewById<TextInputEditText>(R.id.LoginInput)
        val passwordField = findViewById<TextInputEditText>(R.id.mdpInput)

        val login = loginField.text.toString().trim()
        val password = passwordField.text.toString()

        val user = UserData(login, password)

        Api().post<UserData, AuthResponseData>("https://polyhome.lesmoulinsdudev.com/api/users/auth", user, ::loginSuccess)
    }

    private fun loginSuccess(responseCode: Int, response: AuthResponseData?) {
        val errorText = findViewById<TextView>(R.id.errorText)
        errorText.text = ""
        when (responseCode) {
            200 if response != null -> {
                CoroutineScope(Dispatchers.Main).launch {
                    TokenStorage(this@MainActivity).write(response.token)
                    startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                    finish()
                }
            }
            400 -> {
                errorText.text = "Entrez un login et un mot de passe"
            }
            401 -> {
                errorText.text = "Mot de passe incorrect"
            }
            404 -> {
                errorText.text = "Login incconu"
            }
            500 -> {
                errorText.text = "Erreur serveur"
            }
        }
    }

    private fun autoLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            val token = TokenStorage(this@MainActivity).read()

            if (!token.isNullOrBlank()) {
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}