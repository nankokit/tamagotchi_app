package com.example.helpme

import Pet
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var petImageView: ImageView
    private lateinit var petNameTextView: TextView
    private lateinit var foodStatusTextView: TextView
    private lateinit var sleepStatusTextView: TextView
    private lateinit var entertainmentStatusTextView: TextView
    private lateinit var foodImageView: ImageView
    private lateinit var coffeeImageView: ImageView
    private lateinit var laptopImageView: ImageView
    private lateinit var selectedPet: Pet

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = if (resources.configuration.smallestScreenWidthDp >= 600)
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация UI компонентов
        initializeUIComponents()

        // Получение питомца из Intent
        selectedPet = intent.getParcelableExtra("SELECTED_PET") ?: throw IllegalArgumentException("Pet not found")

        // Установка изображения и имени питомца
        updatePetView()

        // Запуск обновления состояния питомца
        startUpdatingPetStatus()

        // Установка слушателей для перетаскивания предметов
        setupTouchListeners()
        setupDragListener()
    }

    private fun initializeUIComponents() {
        petImageView = findViewById(R.id.petImageView)
        petNameTextView = findViewById(R.id.petNameTextView)
        foodStatusTextView = findViewById(R.id.foodStatusTextView)
        sleepStatusTextView = findViewById(R.id.sleepStatusTextView)
        entertainmentStatusTextView = findViewById(R.id.entertainmentStatusTextView)
        foodImageView = findViewById(R.id.foodImageView)
        coffeeImageView = findViewById(R.id.coffeeImageView)
        laptopImageView = findViewById(R.id.laptopImageView)
    }

    private fun updatePetView() {
        petImageView.setImageResource(selectedPet.imageResId)
        petNameTextView.text = selectedPet.name
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListeners() {
        foodImageView.setOnTouchListener { view, motionEvent -> handleTouch(view, motionEvent, "food") }
        coffeeImageView.setOnTouchListener { view, motionEvent -> handleTouch(view, motionEvent, "coffee") }
        laptopImageView.setOnTouchListener { view, motionEvent -> handleTouch(view, motionEvent, "laptop") }
    }

    private fun handleTouch(view: View, motionEvent: MotionEvent, label: String): Boolean {
        return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            startDrag(view, label)
            true
        } else {
            false
        }
    }

    private fun startDrag(view: View, label: String) {
        val clipData = ClipData.newPlainText("label", label)
        val dragShadow = View.DragShadowBuilder(view)
        view.startDragAndDrop(clipData, dragShadow, view, 0)
        Log.d("PetStatus", "Started dragging: $label")
    }

    private fun setupDragListener() {
        petImageView.setOnDragListener { view, dragEvent -> handleDragEvent(dragEvent) }
    }

    private fun handleDragEvent(dragEvent: DragEvent): Boolean {
        return when (dragEvent.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("PetStatus", "Drag entered")
                true
            }
            DragEvent.ACTION_DROP -> {
                if (dragEvent.clipData != null && dragEvent.clipData.itemCount > 0) {
                    val droppedItem = dragEvent.clipData.getItemAt(0).text.toString()
                    Log.d("PetStatus", "Dropped item: $droppedItem")
                    handleDrop(droppedItem)
                    true
                } else {
                    Log.d("PetStatus", "No clip data available")
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("PetStatus", "Drag ended")
                true
            }
            else -> false
        }
    }

    private fun handleDrop(item: String) {
        Log.d("PetStatus", "Handling dropped item: $item")
        when (item) {
            "food" -> {
                selectedPet.feed(10)
                Log.d("PetStatus", "Food increased: ${selectedPet.food}")
            }
            "coffee" -> {
                selectedPet.putToSleep(5)
                Log.d("PetStatus", "Sleep increased: ${selectedPet.sleep}")
            }
            "laptop" -> {
                selectedPet.entertain(5)
                Log.d("PetStatus", "Entertainment increased: ${selectedPet.entertainment}")
            }
            else -> {
                Log.d("PetStatus", "Unknown item dragged.")
            }
        }
        updatePetStatus()
    }

    private fun startUpdatingPetStatus() {
        runnable = Runnable {
            selectedPet.updateStats(1000) // Уменьшение значений каждую секунду
            Log.d("PetStatus", "Updated pet stats: ${selectedPet.getStatus()}")
            updatePetStatus()
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }

    @SuppressLint("SetTextI18n")
    private fun updatePetStatus() {
        foodStatusTextView.text = "Еда: ${selectedPet.food}"
        sleepStatusTextView.text = "Сон: ${selectedPet.sleep}"
        entertainmentStatusTextView.text = "Развлечения: ${selectedPet.entertainment}"
        Log.d("PetStatus", "UI updated: Food: ${selectedPet.food}, Sleep: ${selectedPet.sleep}, Entertainment: ${selectedPet.entertainment}")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        Log.d("PetStatus", "Activity destroyed and updates stopped.")
    }
}