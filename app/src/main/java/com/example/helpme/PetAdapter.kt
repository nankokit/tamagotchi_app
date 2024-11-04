package com.example.helpme

import Pet
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView

class PetAdapter(private val context: Context, private val pets: List<Pet>) : BaseAdapter(),
    ListAdapter {

    override fun getCount(): Int = pets.size

    override fun getItem(position: Int): Pet = pets[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.pet_card, parent, false)

        val petImageView = view.findViewById<ImageView>(R.id.petImageView)
        val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)

        val pet = getItem(position)
        petImageView.setImageResource(pet.imageResId)
        petNameTextView.text = pet.name

        return view
    }
}