package io.github.pablichj.exercisejp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pablichj.exercisejp.data.GeocodeEntry
import io.github.pablichj.exercisejp.data.OpenWeatherApiManager
import io.github.pablichj.exercisejp.domain.GeocodeCityInput
import io.github.pablichj.exercisejp.domain.GeocodeCityOutput
import io.github.pablichj.exercisejp.domain.GeocodeCityUseCase
import io.github.pablichj.exercisejp.domain.UseCaseResult
import io.github.pablichj.exercisejp.util.DispatcherProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultSearchPageViewModel @Inject constructor(
    val dispatcherProvider: DispatcherProvider,
    val geocodeCityUseCase: GeocodeCityUseCase
) : ViewModel(), SearchPageViewModel {

    private val _searchPageState = mutableStateOf<SearchPageState>(SearchPageState.Idle)
    override val searchPageState: State<SearchPageState>
        get() = _searchPageState

    override fun start() {

    }

    override fun doCityGeocodeRequest(city: String, stateCode: String, countryCode: String) {
        viewModelScope.launch(dispatcherProvider.main) {
            processNewGeocodeByCityRequest(
                GeocodeCityInput(
                    apiKey = OpenWeatherApiManager.API_KEY,
                    city = city,
                    stateCode = stateCode,
                    countryCode = countryCode
                )
            )
        }
    }

    private suspend fun processNewGeocodeByCityRequest(
        geocodeCityInput: GeocodeCityInput
    ) {
        _searchPageState.value = SearchPageState.Loading

        when (val geoCodeCityOutput = geocodeCityUseCase.execute(geocodeCityInput)) {
            is UseCaseResult.Error -> {
                _searchPageState.value = SearchPageState.SearchError(geoCodeCityOutput.error)
                return
            }
            is UseCaseResult.Success -> {
                processCityWeather(geoCodeCityOutput.value.geocodeEntry)
            }
        }
    }

    private fun processCityWeather(geocodeEntry: GeocodeEntry) {
        _searchPageState.value = SearchPageState.SearchSuccess(geocodeEntry)
    }

}
