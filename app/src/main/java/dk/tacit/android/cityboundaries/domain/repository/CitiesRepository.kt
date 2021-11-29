package dk.tacit.android.cityboundaries.domain.repository

import dk.tacit.android.cityboundaries.data.remote.dto.CityDto

interface CitiesRepository {
    suspend fun getCities(): List<CityDto>
}