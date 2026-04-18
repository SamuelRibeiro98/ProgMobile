package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.data.HouseListData
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private var token: String? = null
    private var ownerHouse: HouseListData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_activity)

        CoroutineScope(Dispatchers.Main).launch {
            token = TokenStorage(this@MenuActivity).read()

            if (token.isNullOrBlank()) {
                startActivity(Intent(this@MenuActivity, MainActivity::class.java))
                finish()
                return@launch
            }
            loadHouses()
        }
    }

    private fun loadHouses() {
        val currentToken = token ?: return

        Api().get<List<HouseListData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHousesSuccess, currentToken)
    }

    private fun loadHousesSuccess(responseCode: Int, houses: List<HouseListData>?) {
        runOnUiThread {
            if (responseCode == 200 && houses != null) {
                ownerHouse = houses.find { it.owner }
                val idHouseText = findViewById<TextView>(R.id.idHouse)
                idHouseText.text = "ID maison : ${ownerHouse?.houseId ?: "inconnu"}"
            } else if (responseCode == 403) {
                CoroutineScope(Dispatchers.Main).launch {
                    TokenStorage(this@MenuActivity).clear()
                    startActivity(Intent(this@MenuActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                println("Erreur API : $responseCode")
            }
        }
    }

    fun controlHouse(view: View)
    {
        val intentControl = Intent(this, ObjectChoiceActivity::class.java)
        intentControl.putExtra("idHouse", ownerHouse?.houseId)
        startActivity(intentControl)
    }
    fun logout(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            TokenStorage(this@MenuActivity).clear()
            startActivity(Intent(this@MenuActivity, MainActivity::class.java))
            finish()
        }
    }

    fun openOtherHouses(view: View) {
        val intent = Intent(this, OtherHousesActivity::class.java)
        intent.putExtra("token", this.token)
        startActivity(intent)
    }

    fun openRights(view: View) {
        val intent = Intent(this, RightsActivity::class.java)
        intent.putExtra("houseId", ownerHouse?.houseId)
        startActivity(intent)
    }
}