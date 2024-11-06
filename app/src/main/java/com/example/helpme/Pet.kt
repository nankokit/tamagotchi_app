import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import kotlin.random.Random

data class Pet(
    val name: String,
    val imageResId: Int,
    var food: Int = 100,
    var sleep: Int = 100,
    var entertainment: Int = 0
) : Parcelable {
    var entertainmentChanged: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageResId)
        parcel.writeInt(food)
        parcel.writeInt(sleep)
        parcel.writeInt(entertainment)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet = Pet(parcel)
        override fun newArray(size: Int): Array<Pet?> = arrayOfNulls(size)
    }

    fun updateStats(timeElapsed: Long) {
        val randomMultiplierFood = Random.nextInt(1, 6)
        val randomMultiplierSleep = Random.nextInt(1, 6)
        val randomMultiplierEntertainment = Random.nextInt(1, 6)
        food -= (timeElapsed / 1000).toInt() * randomMultiplierFood
        sleep -= (timeElapsed / 1000).toInt() * randomMultiplierSleep
        entertainment -= (timeElapsed / 1000).toInt() * randomMultiplierEntertainment

        food = food.coerceAtLeast(0)
        sleep = sleep.coerceAtLeast(0)
        entertainment = entertainment.coerceAtLeast(0)

        Log.d("PetStats", "Food: $food, Sleep: $sleep, Entertainment: $entertainment")
    }

    fun feed(amount: Int) {
        food += amount
        food = food.coerceAtMost(100)
        Log.d("PetAction", "Fed $amount, Food now: $food")
    }

    fun putToSleep(amount: Int) {
        sleep += amount
        sleep = sleep.coerceAtMost(100)
        Log.d("PetAction", "Put to sleep by $amount, Sleep now: $sleep")
    }

    fun entertain(amount: Int) {
        entertainment += amount
        entertainment = entertainment.coerceAtMost(100)
        entertainmentChanged = true
        Log.d("PetAction", "Entertained by $amount, Entertainment now: $entertainment")
    }

    fun getStatus(): String {
        return "Питомец: $name\nЕда: $food\nСон: $sleep\nЛабы: $entertainment"
    }
}