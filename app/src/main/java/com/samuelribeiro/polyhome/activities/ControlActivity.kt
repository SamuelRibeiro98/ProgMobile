package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.adapters.DevicesAdapter
import com.samuelribeiro.polyhome.data.DeviceCommandData
import com.samuelribeiro.polyhome.data.DevicesData
import com.samuelribeiro.polyhome.data.DevicesResponseData
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ControlActivity : AppCompatActivity() {
    private var idHouse: Int = -1
    private var type: String = ""
    private var token: String? = null
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var listView: ListView

    private var filteredDevices: List<DevicesData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.control_activity)

        idHouse = intent.getIntExtra("idHouse", -1)
        type = intent.getStringExtra("type") ?: ""

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        listView = findViewById(R.id.listView)

        if (idHouse == -1 || type.isBlank()) {
            finish()
        }

        setupPage()

        CoroutineScope(Dispatchers.Main).launch {
            token = TokenStorage(this@ControlActivity).read()
            if (token.isNullOrBlank()) {
                startActivity(Intent(this@ControlActivity, MainActivity::class.java))
                finish()
            }
            loadDevices()
        }
    }

    private fun setupPage() {
        when (type) {
            "light" -> setupLights()
            "rolling shutter", "garage door" -> setupOtherDevices()
            else -> {
                finish()
            }
        }
    }

    private fun setupLights() {
        button1.text = "On"
        button2.text = "Off"
        button3.visibility = View.GONE

        button1.setOnClickListener {
            sendCommandToAllDevices("TURN ON")
        }

        button2.setOnClickListener {
            sendCommandToAllDevices("TURN OFF")
        }
    }

    private fun setupOtherDevices() {
        button1.text = "Open"
        button2.text = "Stop"
        button3.text = "Close"
        button3.visibility = View.VISIBLE

        button1.setOnClickListener {
            sendCommandToAllDevices("OPEN")
        }

        button2.setOnClickListener {
            sendCommandToAllDevices("STOP")
        }

        button3.setOnClickListener {
            sendCommandToAllDevices("CLOSE")
        }
    }

    private fun loadDevices() {
        Api().get<DevicesResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/$idHouse/devices", ::loadDevicesSuccess, token)
    }

    private fun loadDevicesSuccess(responseCode: Int, response: DevicesResponseData?) {
        runOnUiThread {
            if (responseCode == 200 && response != null) {
                filteredDevices = response.devices.filter { it.type == type }

                val currentToken = token
                if (currentToken == null) {
                    Toast.makeText(this, "Token manquant", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }

                val adapter = DevicesAdapter(this, filteredDevices, idHouse, currentToken)
                listView.adapter = adapter

            } else if (responseCode == 200 && response == null) {
                Toast.makeText(this, "Veuillez ouvrir la maison dans un navigateur.", Toast.LENGTH_LONG).show()
                finish()
            } else if (responseCode == 403) {
                Toast.makeText(this, "Session expirée", Toast.LENGTH_LONG).show()
                CoroutineScope(Dispatchers.Main).launch {
                    TokenStorage(this@ControlActivity).clear()
                    startActivity(Intent(this@ControlActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Erreur API : $responseCode", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun sendCommandToAllDevices(command: String) {
        val currentToken = token
        if (currentToken.isNullOrBlank()) {
            Toast.makeText(this, "Token manquant", Toast.LENGTH_SHORT).show()
            return
        }
        if (filteredDevices.isEmpty()) {
            Toast.makeText(this, "Aucun appareil à contrôler", Toast.LENGTH_SHORT).show()
            return
        }
        for (device in filteredDevices) {
            Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$idHouse/devices/${device.id}/command", DeviceCommandData(command), ::sendCommandToAllDevicesSuccess, currentToken)
        }
    }

    private fun sendCommandToAllDevicesSuccess(responseCode: Int) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                }
                403 -> {
                    Toast.makeText(this, "Accès interdit ou session expirée", Toast.LENGTH_SHORT).show()
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

    fun goBack(view: View)
    {
        finish()
    }
}