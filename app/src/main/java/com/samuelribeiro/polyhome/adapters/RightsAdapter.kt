package com.samuelribeiro.polyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.samuelribeiro.polyhome.R
import com.samuelribeiro.polyhome.data.ControllersData

class RightsAdapter(context: Context, private val users: List<ControllersData>, private val onRemoveClick: (ControllersData) -> Unit) : ArrayAdapter<ControllersData>(context, 0, users)
{

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rights_list_element, parent, false)

        val user = users[position]

        val txtUserLogin = view.findViewById<TextView>(R.id.txtUserLogin)
        val btnRemoveAccess = view.findViewById<Button>(R.id.btnRemoveAccess)

        txtUserLogin.text = user.userLogin

        if (user.owner == 1) {
            btnRemoveAccess.visibility = View.GONE
        } else {
            btnRemoveAccess.visibility = View.VISIBLE
            btnRemoveAccess.setOnClickListener {
                onRemoveClick(user)
            }
        }

        return view
    }
}