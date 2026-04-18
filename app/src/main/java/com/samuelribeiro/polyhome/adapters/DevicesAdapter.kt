package com.samuelribeiro.polyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.data.DeviceCommandData
import com.samuelribeiro.polyhome.data.DevicesData
import com.samuelribeiro.polyhome.network.Api

class DevicesAdapter(context: Context, private val devices: List<DevicesData>, private val idHouse: Int, private val token: String) : ArrayAdapter<DevicesData>(context, 0, devices)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.device_list_element, parent, false)

        val device = devices[position]
        val deviceName = view.findViewById<TextView>(R.id.deviceName)
        val buttonContainer = view.findViewById<LinearLayout>(R.id.buttonContainer)

        deviceName.text = device.id

        buttonContainer.removeAllViews()

        for (command in device.availableCommands) {
            val button = Button(context)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            button.text = command
            button.layoutParams = params
            params.marginEnd = 12

            button.setOnClickListener {
                sendCommand(device.id, command)
            }

            buttonContainer.addView(button)
        }

        return view
    }


    private fun sendCommand(deviceId: String, command: String) {
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$idHouse/devices/$deviceId/command", DeviceCommandData(command), ::sendCommandSuccess, token)
    }

    private fun sendCommandSuccess(responseCode: Int) {
        when (responseCode) {
            403 -> {
                Toast.makeText(context, "Accès interdit ou session expirée", Toast.LENGTH_LONG).show()
            }
            500 -> {
                Toast.makeText(context, "Erreur serveur", Toast.LENGTH_SHORT).show()
            }
        }
    }
}