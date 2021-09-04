package com.example.sosnew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Adapter2(private val context : MainActivity,
               private val dataSet: List<DataContact>)
    : RecyclerView.Adapter<Adapter2.ViewHolderItem>() {
    private lateinit var db : MyDatabase
    inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name1)
        val numberPhone: TextView = view.findViewById(R.id.numberPhone1)
        val delete : Button = view.findViewById(R.id.delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item1, null)
        db = MyDatabase(context)
        return ViewHolderItem(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val item = dataSet[position]
        holder.name.text = item.name
        holder.numberPhone.text = item.phoneNumber
        holder.delete.setOnClickListener {
            db.delete(item)
            val number = item.phoneNumber
            db.query()
            Toast.makeText(context,"deleted $number", Toast.LENGTH_LONG).show()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() : Int = dataSet.size
}