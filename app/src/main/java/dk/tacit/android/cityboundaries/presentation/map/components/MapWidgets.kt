package dk.tacit.android.cityboundaries.presentation.map.components

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.ktx.awaitMap
import dk.tacit.android.cityboundaries.R
import dk.tacit.android.cityboundaries.common.extensions.getPolygonLatLngBounds
import kotlinx.coroutines.launch

@Composable
fun GoogleMapBox(
    modifier: Modifier = Modifier,
    location: LatLng,
    polygonPoints: List<LatLng>
) {

    Box(
        modifier = modifier,
    ) {
        val mapView = rememberMapViewWithLifecycle()

        MapViewContainer(
            map = mapView,
            latitude = location.latitude.toString(),
            longitude = location.longitude.toString(),
            polygonPoints = polygonPoints

        )
    }
}

@Composable
private fun MapViewContainer(
    map: MapView,
    latitude: String,
    longitude: String,
    polygonPoints: List<LatLng>
) {
    val cameraPosition = remember(latitude, longitude) {
        LatLng(latitude.toDouble(), longitude.toDouble())
    }

    val coroutineScope = rememberCoroutineScope()
    AndroidView({ map }) { mapView ->
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()
            if (polygonPoints.isEmpty()) {
                googleMap.setOnCameraIdleListener{
                    googleMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(cameraPosition, 10f)
                        )
                    )
                }
            } else {
                val rectOptions = PolygonOptions()
                    .fillColor(R.color.map_polygon)
                    .strokeWidth(0f)
                    .addAll(polygonPoints)
                googleMap.addPolygon(rectOptions)
                googleMap.setOnCameraIdleListener{
                    val latLngBounds = getPolygonLatLngBounds(polygonPoints)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200))
                }
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        // Make MapView follow the current lifecycle
        val lifecycleObserver = getMapLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }
