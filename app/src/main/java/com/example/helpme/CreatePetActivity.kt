package com.example.helpme

class CreatePetActivity : AppCompatActivity() {

    private lateinit var petImageView: ImageView
    private lateinit var petNameTextView: TextView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private val pets = listOf(
        Pet("Собака", R.drawable.dog_image),
        Pet("Кошка", R.drawable.cat_image),
        Pet("Птица", R.drawable.bird_image)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)

        petImageView = findViewById(R.id.petImageView)
        petNameTextView = findViewById(R.id.petNameTextView)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        updatePetCard()

        prevButton.setOnClickListener {
            currentIndex = (currentIndex - 1 + pets.size) % pets.size
            updatePetCard()
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % pets.size
            updatePetCard()
        }
    }

    private fun updatePetCard() {
        val pet = pets[currentIndex]
        petImageView.setImageResource(pet.imageResId)
        petNameTextView.text = pet.name
    }
}