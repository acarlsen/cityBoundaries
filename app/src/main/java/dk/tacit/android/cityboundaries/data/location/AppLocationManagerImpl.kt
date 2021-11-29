package dk.tacit.android.cityboundaries.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.tacit.android.cityboundaries.domain.location.AppLocationManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
class AppLocationManagerImpl(
    @ApplicationContext private val context: Context
) : AppLocationManager {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var lastLocation: Location? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchUpdates(updateIntervalSeconds: Long): Flow<Location> = callbackFlow {
        if (hasLocationPermission()) {
            val req = LocationRequest.create().apply {
                interval = TimeUnit.SECONDS.toMillis(updateIntervalSeconds)
                fastestInterval = TimeUnit.SECONDS.toMillis(5L)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val callBack = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    lastLocation = location
                    trySend(location)
                }
            }

            fusedLocationClient.requestLocationUpdates(req, callBack, Looper.getMainLooper())
            awaitClose { fusedLocationClient.removeLocationUpdates(callBack) }
        } else {
            cancel(CancellationException("PermissionError", null))
        }
    }

    override fun getUsersLastKnownLocation(): Location? {
        return lastLocation
    }

    override fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Location.calculateDistanceInKm(targetLat: Double, targetLon: Double): Int {
    val city = Location("")
    city.latitude = targetLat
    city.longitude = targetLon

    val distanceInMeters: Float = distanceTo(city)
    return (distanceInMeters / 1000).toInt()
}