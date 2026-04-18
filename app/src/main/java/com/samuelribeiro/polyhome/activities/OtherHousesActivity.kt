package com.samuelribeiro.polyhome.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.adapters.OtherHouseAdapter
import com.samuelribeiro.polyhome.data.HouseListData
import com.samuelribeiro.polyhome.network.Api
import com.samuelribeiro.polyhome.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtherHousesActivity : AppCompatActivity() {
    private var token: String? = null
    private var accessibleHouses: List<HouseListData> = emptyList()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.other_houses_activity)

        token = intent.getStringExtra("token")
        listView = findViewById(R.id.otherHousesList)
        loadHouses()
    }

    private fun loadHouses() {
        val currentToken = token ?: return

        Api().get<List<HouseListData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHousesSuccess, currentToken)
    }

    private fun loadHousesSuccess(responseCode: Int, houses: List<HouseListData>?) {
        runOnUiThread {
            if (responseCode == 200 && houses != null) {
                accessibleHouses = houses.filter { !it.owner }

                val adapter = OtherHouseAdapter(this, accessibleHouses)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedHouse = accessibleHouses[position]

                    val intent = Intent(this, ObjectChoiceActivity::class.java)
                    intent.putExtra("idHouse", selectedHouse.houseId)
                    startActivity(intent)
                }
            } else if (responseCode == 403) {
                CoroutineScope(Dispatchers.Main).launch {
                    TokenStorage(this@OtherHousesActivity).clear()
                    startActivity(Intent(this@OtherHousesActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                println("Erreur API : $responseCode")
            }
        }
    }

    fun goBack(view: View) {
        finish()
    }
}