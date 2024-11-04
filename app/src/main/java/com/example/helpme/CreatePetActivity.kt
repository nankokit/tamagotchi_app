package com.example.helpme

import Pet
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreatePetActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var petAdapter: PetAdapter
    private var selectedPet: Pet? = null

    private val pets = listOf(
        Pet("ВМСиС", R.drawable.kitty_2),
        Pet("ИИТП", R.drawable.kitty_1),
        Pet("ПОИТ", R.drawable.kitty_3),
        Pet("ЭВС", R.drawable.kitty_4)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)
        requestedOrientation = if (resources.configuration.smallestScreenWidthDp >= 600)
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        gridView = findViewById(R.id.gridView)
        petAdapter = PetAdapter(this, pets)
        gridView.adapter = petAdapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedPet = petAdapter.getItem(position)
            Toast.makeText(this, "Вы выбрали: ${selectedPet?.name}", Toast.LENGTH_SHORT).show()
        }

        val buttonNext = findViewById<Button>(R.id.buttonNext)
        buttonNext.setOnClickListener {
            if (selectedPet != null) {
                // Переход на MainActivity с выбранным питомцем
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("SELECTED_PET", selectedPet)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Пожалуйста, выберите питомца", Toast.LENGTH_SHORT).show()
            }
        }
    }
}