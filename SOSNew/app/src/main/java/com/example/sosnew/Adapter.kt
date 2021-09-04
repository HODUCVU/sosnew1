package com.example.sosnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val context : MainActivity,
    private val dataSet: List<DataContact>)
    : RecyclerView.Adapter<Adapter.ViewHolderItem>() {
    private lateinit var db : MyDatabase
    inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val numberPhone: TextView = view.findViewById(R.id.numberPhone)
        val add : Button = view.findViewById(R.id.add)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, null)
        db = MyDatabase(context)
        return ViewHolderItem(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val item = dataSet[position]
        holder.name.text = item.name
        holder.numberPhone.text = item.phoneNumber
        holder.add.setOnClickListener {
            db.insert(item)
            val number = item.phoneNumber
            db.query()
            Toast.makeText(context,"selected $number", Toast.LENGTH_LONG).show()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() : Int = dataSet.size
}