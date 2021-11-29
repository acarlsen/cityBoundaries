package dk.tacit.android.cityboundaries.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface AppLocationManager {
    fun getUsersLastKnownLocation(): Location?
    fun fetchUpdates(updateIntervalSeconds: Long): Flow<Location>
    fun hasLocationPermission(): Boolean
}