package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.data.DevicesResponseData
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ObjectChoiceActivity : AppCompatActivity() {

    private var token: String? = null
    private var idHouse: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.object_choice_activity)

        idHouse = intent.getIntExtra("idHouse", -1)

        CoroutineScope(Dispatchers.Main).launch {
            token = TokenStorage(this@ObjectChoiceActivity).read()

            if (token != null && idHouse != -1) {
                loadDevices()
            }
        }
    }

    private fun loadDevices() {
        Api().get<DevicesResponseData>("https://polyhome.lesmoulinsdudev.com/api/houses/$idHouse/devices", ::loadDevicesSuccess, token)
    }

    private fun loadDevicesSuccess(responseCode: Int, response: DevicesResponseData?) {
        runOnUiThread {
            if(responseCode == 200 && response == null) {
                Toast.makeText(this, "Veuillez ouvrir la maison dans un navigateur ou recharger la page.", Toast.LENGTH_LONG).show()
                finish()
            } else if (responseCode == 403) {
                Toast.makeText(this, "Vous n'avez pas accès à cette maison.", Toast.LENGTH_LONG).show()
                finish()
            } else if (responseCode == 500) {
                Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                finish()
            } else if (responseCode != 200){
                Toast.makeText(this, "Données incorrectes", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    fun controlShutters(view: View)
    {
        val intent = Intent(this, ControlActivity::class.java)
        intent.putExtra("type", "rolling shutter")
        intent.putExtra("idHouse", this.idHouse)
        startActivity(intent)
    }
    fun controlGarage(view: View)
    {
        val intent = Intent(this, ControlActivity::class.java)
        intent.putExtra("type", "garage door")
        intent.putExtra("idHouse", this.idHouse)
        startActivity(intent)
    }
    fun controlLights(view: View)
    {
        val intent = Intent(this, ControlActivity::class.java)
        intent.putExtra("type", "light")
        intent.putExtra("idHouse", this.idHouse)
        startActivity(intent)
    }

    fun goBack(view: View)
    {
        finish()
    }
}