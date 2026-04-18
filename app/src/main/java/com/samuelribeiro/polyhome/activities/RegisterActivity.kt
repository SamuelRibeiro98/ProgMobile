package com.samuelribeiro.polyhome.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.data.UserData

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
    }

    public fun register(view: View) {
        val loginField = findViewById<TextInputEditText>(R.id.LoginInput)
        val passwordField = findViewById<TextInputEditText>(R.id.mdpInput)
        val passwordConfirmationField = findViewById<TextInputEditText>(R.id.mdpConfirmationInput)

        val login = loginField.text.toString()
        if (passwordField.text.toString() == passwordConfirmationField.text.toString()) {
            val password = passwordField.text.toString()
            val userData = UserData(login, password)
            Api().post<UserData>("https://polyhome.lesmoulinsdudev.com/api/users/register", userData, ::registerSuccess)
        } else {
            val errorText = findViewById<TextView>(R.id.errorText2)
            errorText.text = "Les deux mots de passe ne correspondent pas"
        }
    }
    fun registerSuccess(responseCode: Int) {
        val errorText = findViewById<TextView>(R.id.errorText2)
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Compte créé", Toast.LENGTH_SHORT).show()
                    finish()
                }
                400 -> {
                    errorText.text = "Entrez un login et un mot de passe"
                }
                409 -> {
                    errorText.text = "Login déja utilisé"
                }
                500 -> {
                    errorText.text = "Erreur serveur"
                }
            }
        }
    }
}