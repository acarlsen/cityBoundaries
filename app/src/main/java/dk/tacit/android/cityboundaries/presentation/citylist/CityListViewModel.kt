package dk.tacit.android.cityboundaries.presentation.citylist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.tacit.android.cityboundaries.data.location.calculateDistanceInKm
import dk.tacit.android.cityboundaries.data.remote.dto.toCity
import dk.tacit.android.cityboundaries.domain.location.AppLocationManager
import dk.tacit.android.cityboundaries.domain.model.City
import dk.tacit.android.cityboundaries.domain.repository.CitiesRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val citiesRepository: CitiesRepository,
    private val appLocationManager: AppLocationManager
) : ViewModel() {

    var cities by mutableStateOf(listOf<City>())
    private val _state = MutableStateFlow<CityListViewState>(CityListViewState.Empty)
    val state: StateFlow<CityListViewState> = _state

    init {
        getCities()
        listenForLocationUpdates()
    }

    private fun getCities() {
        viewModelScope.launch {
            try {
                _state.value = CityListViewState.Loading
                 val result = citiesRepository.getCities().map { it.toCity() }
                cities = result
                _state.value = CityListViewState.CityList
            } catch (e: Exception) {
                _state.value = CityListViewState.LoadError
            }
        }
    }

    fun onCityClick(city: City) {
        _state.value = CityListViewState.CityDetails(city)
    }

    fun onBackClick() {
        _state.value = CityListViewState.CityList
    }

    fun onBackPressed(): Boolean {
        if (_state.value is CityListViewState.CityDetails) {
            onBackClick()
            return true
        }
        return false
    }

    @OptIn(InternalCoroutinesApi::class)
    fun listenForLocationUpdates() {
        viewModelScope.launch {
            appLocationManager
                .fetchUpdates(10L)
                .collect { location ->
                    // TODO - check for similar location to avoid unnecessary updates
                    cities = cities.map {
                        it.copy(distanceUserKm = location.calculateDistanceInKm(it.lat, it.lon))
                    }
                }
        }
    }
}

