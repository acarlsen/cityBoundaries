package dk.tacit.android.cityboundaries.data.repository

import dk.tacit.android.cityboundaries.data.remote.CitiesApi
import dk.tacit.android.cityboundaries.data.remote.dto.CityDto
import dk.tacit.android.cityboundaries.domain.repository.CitiesRepository

class CitiesRepositoryImpl(
    private val api: CitiesApi
) : CitiesRepository {
    override suspend fun getCities(): List<CityDto> {
        return api.getCities().cities
    }
}