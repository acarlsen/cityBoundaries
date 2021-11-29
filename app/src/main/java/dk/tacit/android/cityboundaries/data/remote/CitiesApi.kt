package dk.tacit.android.cityboundaries.data.remote

import dk.tacit.android.cityboundaries.data.remote.dto.CitiesResultDto
import retrofit2.http.GET

interface CitiesApi {
    @GET("cities")
    suspend fun getCities(): CitiesResultDto
}