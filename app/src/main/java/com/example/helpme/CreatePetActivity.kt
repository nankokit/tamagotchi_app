package com.example.helpme

import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreatePetActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var petAdapter: PetAdapter

    private val pets = listOf(
        Pet("ВМСиС", R.drawable.kitty_2),
        Pet("ИИТП", R.drawable.kitty_1),
        Pet("ПОИТ", R.drawable.kitty_3),
        Pet("ЭВС", R.drawable.kitty_4)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)

        gridView = findViewById(R.id.gridView)
        petAdapter = PetAdapter(this, pets)
        gridView.adapter = petAdapter

        // Обработка нажатий на элемент GridView
        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedPet = petAdapter.getItem(position)
            Toast.makeText(this, "Вы выбрали: ${selectedPet.name}", Toast.LENGTH_SHORT).show()
        }
    }
}