package io.github.pablichj.exercisejp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pablichj.exercisejp.data.OpenWeatherApiManager
import io.github.pablichj.exercisejp.domain.GeocodeCityInput
import io.github.pablichj.exercisejp.domain.GeocodeCityUseCase
import io.github.pablichj.exercisejp.domain.GetCityWeatherInput
import io.github.pablichj.exercisejp.domain.GetCityWeatherUseCase
import io.github.pablichj.exercisejp.domain.UseCaseResult
import io.github.pablichj.exercisejp.util.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    fun start(context: Context, permission: String) {
        tryCurrentLocation(context, permission)
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

    private fun updatePermissionState(shouldAsk: Boolean) {
        val currentPageState = _searchPageState.value
        _searchPageState.value = currentPageState.copy(
            shouldAskLocationPermission = shouldAsk
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
                val lat = geoCodeCityOutput.value.geocodeEntry.lat
                val lon = geoCodeCityOutput.value.geocodeEntry.lon
                if (lat == null || lon == null) {
                    updateWeatherSectionState(WeatherSectionState.SearchError("Invalid Lat or Lon"))
                    return
                }

                processCityWeather(lat, lon, cityWeatherRequest.units)
            }
        }
    }

    private suspend fun processCityWeather(lat: Double, lon: Double, units: Units) {

        val cityWeatherInput = GetCityWeatherInput(
            apiKey = OpenWeatherApiManager.API_KEY,
            lat = lat.toString(),
            lon = lon.toString(),
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

    private fun tryCurrentLocation(context: Context, permission: String) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            updatePermissionState(true)
        } else {
            presentCurrentLocationWeather(context)
        }
    }

    fun locationPermissionResult(context: Context, granted: Boolean) {
        if (granted) {
            presentCurrentLocationWeather(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun presentCurrentLocationWeather(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            updateWeatherSectionState(WeatherSectionState.Loading)
            val result = try {
                val locationClient = LocationServices.getFusedLocationProviderClient(context)
                val task = locationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                )
                task.await()
            } catch (th: Throwable) {
                null
            }

            if (result != null) {
                Log.d("VM", "Pablo location: ${result.latitude}")
                processCityWeather(
                    result.latitude,
                    result.longitude,
                    searchPageState.value.searchFormState.units
                )
            } else {
                updateWeatherSectionState(
                    WeatherSectionState.SearchError(
                        "Location Permission is granted but there were some issues getting your location"
                    )
                )
            }
        }
    }

    fun clearShouldAskLocationPermission() {
        updatePermissionState(false)
    }

}
