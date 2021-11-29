package dk.tacit.android.cityboundaries.domain.model

data class City(
    val name: String,
    val lat: Double,
    val lon : Double,
    val r: Long,
    val points: String,
    val distanceUserKm: Int?
)
