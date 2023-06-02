package io.github.pablichj.exercisejp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pablichj.exercisejp.data.GeocodeEntry
import io.github.pablichj.exercisejp.data.OpenWeatherApiManager
import io.github.pablichj.exercisejp.domain.GeocodeCityInput
import io.github.pablichj.exercisejp.domain.GeocodeCityUseCase
import io.github.pablichj.exercisejp.domain.GetCityWeatherInput
import io.github.pablichj.exercisejp.domain.GetCityWeatherUseCase
import io.github.pablichj.exercisejp.domain.UseCaseResult
import io.github.pablichj.exercisejp.util.DispatcherProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPageViewModel @Inject constructor(
    val dispatcherProvider: DispatcherProvider,
    val geocodeCityUseCase: GeocodeCityUseCase,
    val getCityWeatherUseCase: GetCityWeatherUseCase
) : ViewModel() {

    private val _searchPageState = mutableStateOf(SearchPageState())
    val searchPageState: State<SearchPageState>
        get() = _searchPageState

    fun start() {

    }

    // region: State Section Updaters

    private fun updateSearchFormState(newSearchFormState: SearchFormState) {
        val currentPageState = _searchPageState.value
        _searchPageState.value = currentPageState.copy(
            searchFormState = newSearchFormState
        )
    }

    private fun updateWeatherSectionState(newWeatherSectionState: WeatherSectionState) {
        val currentPageState = _searchPageState.value
        _searchPageState.value = currentPageState.copy(
            weatherSectionState = newWeatherSectionState
        )
    }

    // endregion

    fun doCityGeocodeRequest(cityWeatherRequest: CityWeatherRequest) {
        viewModelScope.launch(dispatcherProvider.main) {
            processNewGeocodeByCityRequest(cityWeatherRequest)
        }
    }

    private suspend fun processNewGeocodeByCityRequest(
        cityWeatherRequest: CityWeatherRequest
    ) {
        updateWeatherSectionState(WeatherSectionState.Loading)

        val geocodeCityInput = GeocodeCityInput(
            apiKey = OpenWeatherApiManager.API_KEY,
            city = cityWeatherRequest.city,
            stateCode = cityWeatherRequest.stateCode,
            countryCode = cityWeatherRequest.countryCode
        )

        when (val geoCodeCityOutput = geocodeCityUseCase.execute(geocodeCityInput)) {
            is UseCaseResult.Error -> {
                updateWeatherSectionState(WeatherSectionState.SearchError(geoCodeCityOutput.error))
                return
            }

            is UseCaseResult.Success -> {
                processCityWeather(geoCodeCityOutput.value.geocodeEntry, cityWeatherRequest.units)
            }
        }
    }

    private suspend fun processCityWeather(geocodeEntry: GeocodeEntry, units: Units) {

        val cityWeatherInput = GetCityWeatherInput(
            apiKey = OpenWeatherApiManager.API_KEY,
            lat = geocodeEntry.lat.toString(),
            lon = geocodeEntry.lon.toString(),
            units = units.apiValue
        )

        when (val cityWeatherOutput = getCityWeatherUseCase.execute(cityWeatherInput)) {
            is UseCaseResult.Error -> {
                updateWeatherSectionState(WeatherSectionState.SearchError(cityWeatherOutput.error))
                return
            }

            is UseCaseResult.Success -> {
                updateWeatherSectionState(
                    WeatherSectionState.SearchSuccess(cityWeatherOutput.value.cityWeather)
                )
            }
        }
    }

}
