package dk.tacit.android.cityboundaries.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import dk.tacit.android.cityboundaries.presentation.citylist.CityListViewModel
import dk.tacit.android.cityboundaries.presentation.citylist.components.CitiesScreen
import dk.tacit.android.cityboundaries.presentation.theme.CityBoundariesTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CityListViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                viewModel.listenForLocationUpdates()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                viewModel.listenForLocationUpdates()
            }
            else -> {
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForLocationPermission()
        setContent {
            CityBoundariesTheme {
                val state = viewModel.state.collectAsState()
                val cities = viewModel.cities
                CitiesScreen(
                    state = state.value,
                    cities = cities,
                    onCityClick = { city -> viewModel.onCityClick(city) },
                    onBackClick = { viewModel.onBackClick() }
                )
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    private fun askForLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
