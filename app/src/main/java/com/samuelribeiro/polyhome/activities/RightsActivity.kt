package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.adapters.RightsAdapter
import com.samuelribeiro.polyhome.data.ControllersData
import com.samuelribeiro.polyhome.data.RightsRequestData
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RightsActivity : AppCompatActivity() {

    private var token: String? = null
    private var houseId: Int = -1
    private var users: List<ControllersData> = emptyList()

    private lateinit var inputUserLogin: TextInputEditText
    private lateinit var btnAddAccess: Button
    private lateinit var listRightsUsers: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.rights_activity)

        houseId = intent.getIntExtra("houseId", -1)

        inputUserLogin = findViewById(R.id.inputUserLogin)
        btnAddAccess = findViewById(R.id.btnAddAccess)
        listRightsUsers = findViewById(R.id.listRightsUsers)

        btnAddAccess.setOnClickListener {
            addAccess()
        }

        CoroutineScope(Dispatchers.Main).launch {
            token = TokenStorage(this@RightsActivity).read()

            if (token.isNullOrBlank()) {
                startActivity(Intent(this@RightsActivity, MainActivity::class.java))
                finish()
            }

            loadUsers()
        }
    }
    private fun loadUsers() {
        val currentToken = token ?: return

        Api().get<List<ControllersData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", ::loadUsersSuccess, currentToken)
    }

    private fun loadUsersSuccess(responseCode: Int, response: List<ControllersData>?) {
        runOnUiThread {
            if (responseCode == 200 && response != null) {
                users = response
                refreshList()
            } else if (responseCode == 403) {
                logoutAndReturnToLogin()
            } else {
                Toast.makeText(this, "Erreur API : $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshList() {
        val adapter = RightsAdapter(this, users) { user ->
            removeAccess(user.userLogin)
        }
        listRightsUsers.adapter = adapter
    }

    private fun addAccess() {
        val currentToken = token ?: return

        val login = inputUserLogin.text?.toString()?.trim().orEmpty()

        if (login.isBlank()) {
            Toast.makeText(this, "Veuillez saisir un login", Toast.LENGTH_SHORT).show()
            return
        }

        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", RightsRequestData(login), ::addAccessSuccess, currentToken)
    }

    private fun addAccessSuccess(responseCode: Int) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Accès accordé", Toast.LENGTH_SHORT).show()
                    inputUserLogin.setText("")
                    loadUsers()
                }
                403 -> {
                    logoutAndReturnToLogin()
                }
                409 -> {
                    Toast.makeText(this, "Cet utilisateur a déjà accès à la maison", Toast.LENGTH_SHORT).show()
                }
                400 -> {
                    Toast.makeText(this, "Données incorrectes", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Erreur API : $responseCode", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeAccess(userLogin: String) {
        val currentToken = token ?: return

        Api().delete("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users", RightsRequestData(userLogin), ::removeAccessSuccess, currentToken)
    }

    private fun removeAccessSuccess(responseCode: Int) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Accès supprimé", Toast.LENGTH_SHORT).show()
                    loadUsers()
                }
                403 -> {
                    logoutAndReturnToLogin()
                }
                400 -> {
                    Toast.makeText(this, "Données incorrectes", Toast.LENGTH_SHORT).show()
                }
                500 -> {
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Erreur API : $responseCode", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logoutAndReturnToLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            TokenStorage(this@RightsActivity).clear()
            startActivity(Intent(this@RightsActivity, MainActivity::class.java))
            finish()
        }
    }

    fun goBack(view: View) {
        finish()
    }
}