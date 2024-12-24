package com.example.mydialer

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class Adapter(val clickInterface: ClickInterface) : ListAdapter<Contact, Adapter.ViewHolder>(ContactDiffCallback()){

    private val contacts = mutableListOf<Contact>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textPhone: TextView = itemView.findViewById(R.id.textPhone)
        val textType: TextView = itemView.findViewById(R.id.textType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rview_item, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.textName.text = contact.name
        holder.textPhone.text = contact.phone
        holder.textType.text = contact.type

        holder.itemView.setOnClickListener{
            clickInterface.onContactClick(contacts[position])
        }

    }

    fun setItems(items: List<Contact>) {
        this.contacts.clear()
        this.contacts.addAll(items)
    }
}

interface ClickInterface {
    fun onContactClick(contact: Contact)
}