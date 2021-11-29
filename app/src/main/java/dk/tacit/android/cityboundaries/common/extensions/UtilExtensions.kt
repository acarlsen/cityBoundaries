package dk.tacit.android.cityboundaries.common.extensions

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun String.toLatLngList(): List<LatLng> {
    val result = mutableListOf<LatLng>()
    val points = this.split(",")
    points.forEach {
        val latLng = it.split(" ")
        if (latLng.size == 2) {
            result.add(LatLng(latLng[1].toDouble(), latLng[0].toDouble()))
        }
    }
    return result
}

fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds {
    val centerBuilder = LatLngBounds.builder()
    for (point in polygon) {
        centerBuilder.include(point)
    }
    return centerBuilder.build()
}