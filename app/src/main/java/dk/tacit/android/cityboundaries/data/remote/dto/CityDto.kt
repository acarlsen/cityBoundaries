package dk.tacit.android.cityboundaries.data.remote.dto

import dk.tacit.android.cityboundaries.domain.model.City

data class CityDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val r: Long,
    val points: String
)

fun CityDto.toCity(): City {
    return City(
        name = name,
        lat = lat,
        lon = lon,
        r = r,
        points = points,
        distanceUserKm = null
    )
}

