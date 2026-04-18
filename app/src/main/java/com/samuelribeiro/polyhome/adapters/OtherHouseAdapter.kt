package com.samuelribeiro.polyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.data.HouseListData

class OtherHouseAdapter(context: Context, private val houses: List<HouseListData>) : ArrayAdapter<HouseListData>(context, 0, houses)
{

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.other_houses_list_element, parent, false)
        val house = houses[position]
        val txtHouseTitle = view.findViewById<TextView>(R.id.txtHouseTitle)

        txtHouseTitle.text = "Maison ${house.houseId}"

        return view
    }
}