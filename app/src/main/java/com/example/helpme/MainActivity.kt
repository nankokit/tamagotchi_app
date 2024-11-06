package com.example.helpme

import Pet
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

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
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var isGameOver = false

    private val petItemDragListener = View.OnDragListener { view, dragEvent ->
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d("PetStatus", "Drag started")
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("PetStatus", "Drag entered")
//                view.setBackgroundColor(Color.LTGRAY)
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                Log.d("PetStatus", "Drag location: ${dragEvent.x}, ${dragEvent.y}")
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("PetStatus", "Drag exited")
//                view.setBackgroundColor(Color.TRANSPARENT)
                true
            }

            DragEvent.ACTION_DROP -> {
                Log.d("PetStatus", "Drop event")
//                view.setBackgroundColor(Color.TRANSPARENT)
                if (dragEvent.clipData != null && dragEvent.clipData.itemCount > 0) {
                    val droppedItem = dragEvent.clipData.getItemAt(0).text.toString()
                    handleDrop(droppedItem) // Обработка сброса
                    updatePetStatus() // Обновляем статус питомца
                    true
                } else {
                    Log.d("PetStatus", "No clip data available")
                    false
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("PetStatus", "Drag ended")
                view.setBackgroundColor(Color.TRANSPARENT)
                true
            }

            else -> {
                Log.d("PetStatus", "Unhandled drag event: ${dragEvent.action}")
                false
            }
        }
    }

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

        initializeUIComponents()

        selectedPet = intent.getParcelableExtra("SELECTED_PET")
            ?: throw IllegalArgumentException("Pet not found")

        updatePetView()

        startUpdatingPetStatus()

        setupTouchListeners()
        setupDragListener()
        drawerLayout = findViewById(R.id.main)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val menuIcon: View = findViewById(R.id.menuIcon)
        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_restart -> {
                    restartGame()
                    true
                }

                R.id.nav_logout -> {
                    logout()
                    true
                }

                R.id.nav_exit -> {
                    finishAffinity() // Закрыть приложение
                    true
                }

                else -> false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("food", selectedPet.food)
        outState.putInt("sleep", selectedPet.sleep)
        outState.putInt("entertainment", selectedPet.entertainment)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedPet.food = savedInstanceState.getInt("food")
        selectedPet.sleep = savedInstanceState.getInt("sleep")
        selectedPet.entertainment = savedInstanceState.getInt("entertainment")
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
        foodImageView.setOnTouchListener { view, motionEvent ->
            handleTouch(view, motionEvent, "food")
        }
        coffeeImageView.setOnTouchListener { view, motionEvent ->
            handleTouch(view, motionEvent, "coffee")
        }
        laptopImageView.setOnTouchListener { view, motionEvent ->
            handleTouch(view, motionEvent, "laptop")
        }
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
        petImageView.setOnDragListener(petItemDragListener)
    }


    private fun handleDrop(item: String) {
        Log.d("PetStatus", "Handling dropped item: $item")
        if (isGameOver) {
            Log.d("PetStatus", "Cannot handle drop; game is over.")
            return // Не обрабатываем сброс, если игра окончена
        }
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
                selectedPet.putToSleep(-10)
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
            if (!isGameOver) {
                selectedPet.updateStats(1000)
                //Log.d("PetStatus", "Updated pet stats: ${selectedPet.getStatus()}")
                updatePetStatus()
            }
            handler.postDelayed(runnable, 2000)
        }
        handler.post(runnable)
    }

    private fun updatePetStatus() {
        foodStatusTextView.text = "Еда: ${selectedPet.food}"
        sleepStatusTextView.text = "Сон: ${selectedPet.sleep}"
        entertainmentStatusTextView.text = "Лабы: ${selectedPet.entertainment}"
        Log.d(
            "PetStatus",
            "UI updated: Food: ${selectedPet.food}, Sleep: ${selectedPet.sleep}, Entertainment: ${selectedPet.entertainment}"
        )


        if (selectedPet.food <= 0) {
            endGame("food")
        } else if (selectedPet.sleep <= 0) {
            endGame("sleep")
        } else if (selectedPet.entertainment <= 0 && selectedPet.entertainmentChanged) {
            endGame("entertainment")
        }
        if (selectedPet.entertainment == 100) {
            winGame()
        }
    }

    private fun winGame() {
        isGameOver = true
        handler.removeCallbacks(runnable)

        val dialogView = layoutInflater.inflate(R.layout.dialog_win, null)
        val dialogImageView = dialogView.findViewById<ImageView>(R.id.winImageView)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.winMessage)
        val exitButton = dialogView.findViewById<Button>(R.id.exitButton)
        val createPetButton = dialogView.findViewById<Button>(R.id.createPetButton)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        exitButton.setOnClickListener {

            finishAffinity()
        }

        createPetButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreatePetActivity::class.java
                )
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun endGame(reason: String) {
        isGameOver = true
        handler.removeCallbacks(runnable)

        val dialogView = layoutInflater.inflate(R.layout.dialog_game_over, null)
        val dialogImageView = dialogView.findViewById<ImageView>(R.id.dialogImageView)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val exitButton = dialogView.findViewById<Button>(R.id.exitButton)
        val createPetButton = dialogView.findViewById<Button>(R.id.createPetButton)

        dialogMessage.text = when (reason) {
            "food" -> "Рамен остыл в ваших отношениях! Игра окончена."
            "sleep" -> "В молекуле нет твоего любимого сиропа! Игра окончена."
            "entertainment" -> "У кого-то лапки, но не лабки! Игра окончена."
            else -> "Игра окончена."
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        exitButton.setOnClickListener {

            finishAffinity()
        }

        createPetButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreatePetActivity::class.java
                )
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun restartGame() {
        // Логика для перезапуска игры
        val intent = Intent(this, CreatePetActivity::class.java)
        startActivity(intent)
        finish() // Закрывает текущую активность
    }

    private fun logout() {
        // Логика выхода из аккаунта
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрывает текущую активность
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        Log.d("PetStatus", "Activity destroyed and updates stopped.")
    }
}