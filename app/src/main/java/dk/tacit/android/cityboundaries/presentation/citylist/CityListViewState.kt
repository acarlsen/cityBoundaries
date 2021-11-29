package dk.tacit.android.cityboundaries.presentation.citylist

import dk.tacit.android.cityboundaries.domain.model.City

sealed class CityListViewState {
    object Empty : CityListViewState()
    object Loading : CityListViewState()
    object LoadError : CityListViewState()
    object CityList : CityListViewState()
    data class CityDetails(val city: City) : CityListViewState()
}
