package dk.tacit.android.cityboundaries.presentation.citylist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import dk.tacit.android.cityboundaries.R
import dk.tacit.android.cityboundaries.common.extensions.toLatLngList
import dk.tacit.android.cityboundaries.domain.model.City
import dk.tacit.android.cityboundaries.presentation.citylist.CityListViewState
import dk.tacit.android.cityboundaries.presentation.map.components.GoogleMapBox
import dk.tacit.android.cityboundaries.presentation.theme.CityBoundariesTheme

@Composable
fun CitiesScreen(
    state: CityListViewState,
    cities: List<City>,
    onCityClick: (City) -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state is CityListViewState.CityDetails) {
                        Text(text = stringResource(id = R.string.city_boundary))
                    } else {
                        Text(text = stringResource(id = R.string.cities))
                    }
                },
                navigationIcon = {
                    if (state is CityListViewState.CityDetails) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                            )
                        }
                    } else {
                        Icon(
                            modifier = Modifier.padding(all = 8.dp),
                            imageVector = Icons.Filled.Home,
                            contentDescription = stringResource(id = R.string.home),
                        )
                    }
                }
            )
        })
    {
        when (state) {
            is CityListViewState.Empty -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.no_cities)
                    )
                }
            }
            is CityListViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.loading)
                    )
                }
            }
            is CityListViewState.LoadError -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.error_load)
                    )
                }
            }
            is CityListViewState.CityList -> {
                CityListScreen(
                    listState = listState,
                    cities = cities,
                    onCityClick = onCityClick
                )
            }
            is CityListViewState.CityDetails -> {
                CityDetailScreen(city = state.city)
            }
        }
    }
}

@Composable
fun CityListScreen(
    listState: LazyListState,
    cities: List<City>,
    onCityClick: (City) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(cities) { city ->
            CityInfoCard(
                modifier = Modifier.clickable { onCityClick(city) },
                city = city
            )
        }
    }
}

@Composable
fun CityDetailScreen(
    city: City,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        CityInfoCard(
            city = city
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoogleMapBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            location = LatLng(city.lat, city.lon),
            polygonPoints = city.points.toLatLngList()
        )
    }
}

@Composable
fun CityInfoCard(
    modifier: Modifier = Modifier,
    city: City,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.6f)) {
                Text(text = city.name, style = MaterialTheme.typography.h6)
                Text(text = "${city.lat},${city.lon}", style = MaterialTheme.typography.body1)
            }
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .background(MaterialTheme.colors.secondary)
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.distance),
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(
                        id = R.string.distance_km,
                        city.distanceUserKm?.toString() ?: "??"
                    ), style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CityListPreview() {
    CityBoundariesTheme {
        CitiesScreen(
            state = CityListViewState.CityList,
            cities =
            listOf(
                City(
                    name = "Aarhus",
                    lat = 0.00,
                    lon = 0.00,
                    r = 5000,
                    points = "",
                    distanceUserKm = 34
                )
            ),
            onCityClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CityDetailsPreview() {
    CityBoundariesTheme {
        CitiesScreen(
            state = CityListViewState.CityDetails(
                City(
                    name = "Aarhus",
                    lat = 0.00,
                    lon = 0.00,
                    r = 5000,
                    points = "",
                    distanceUserKm = 34
                )
            ),
            cities = listOf(),
            onCityClick = {},
            onBackClick = {}
        )
    }
}