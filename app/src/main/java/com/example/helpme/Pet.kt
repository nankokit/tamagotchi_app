package com.example.helpme

data class Pet(
    val name: String,
    val imageResId: Int,
    var food: Int = 100,
    var sleep: Int = 100,
    var entertainment: Int = 100
) {
    fun updateStats(timeElapsed: Long) {
        food -= (timeElapsed / 1000).toInt()
        sleep -= (timeElapsed / 2000).toInt()
        entertainment -= (timeElapsed / 1500).toInt()


        food = food.coerceAtLeast(0)
        sleep = sleep.coerceAtLeast(0)
        entertainment = entertainment.coerceAtLeast(0)
    }


    fun feed(amount: Int) {
        food += amount
        food = food.coerceAtMost(100)
    }


    fun putToSleep(amount: Int) {
        sleep += amount
        sleep = sleep.coerceAtMost(100)
    }


    fun entertain(amount: Int) {
        entertainment += amount
        entertainment = entertainment.coerceAtMost(100)
    }


    fun getStatus(): String {
        return "Питомец: $name\nЕда: $food\nСон: $sleep\nРазвлечения: $entertainment"
    }
}