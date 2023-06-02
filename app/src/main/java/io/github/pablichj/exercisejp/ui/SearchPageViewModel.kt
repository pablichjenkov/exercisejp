package io.github.pablichj.exercisejp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
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
import io.github.pablichj.exercisejp.data.CityWeatherInfo
import io.github.pablichj.exercisejp.data.OpenWeatherApiManager
import io.github.pablichj.exercisejp.domain.GeocodeCityInput
import io.github.pablichj.exercisejp.domain.GeocodeCityUseCase
import io.github.pablichj.exercisejp.domain.GetCityWeatherInput
import io.github.pablichj.exercisejp.domain.GetCityWeatherUseCase
import io.github.pablichj.exercisejp.domain.UseCaseResult
import io.github.pablichj.exercisejp.util.DispatcherProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SearchPageViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val preferences: SharedPreferences,
    private val geocodeCityUseCase: GeocodeCityUseCase,
    private val getCityWeatherUseCase: GetCityWeatherUseCase
) : ViewModel() {

    private val _searchPageState = mutableStateOf(SearchPageState())
    val searchPageState: State<SearchPageState>
        get() = _searchPageState

    init {
        val searchForm = ModelUtils.createSearchFormStateFromPreferences(preferences)
        updateSearchFormState(searchForm)
        val request = CityWeatherRequest(
            city = searchForm.citySearched,
            stateCode = searchForm.stateSearched,
            countryCode = searchForm.countrySearched,
            units = searchForm.units
        )
        doCityGeocodeRequest(request)
    }

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

    private fun updateShowPermissionState(shouldAsk: Boolean) {
        val currentPageState = _searchPageState.value
        _searchPageState.value = currentPageState.copy(
            shouldAskLocationPermission = shouldAsk
        )
    }

    // endregion

    fun doCityGeocodeRequest(cityWeatherRequest: CityWeatherRequest) {
        viewModelScope.launch(dispatcherProvider.main) {
            processGeocodeByCityRequest(cityWeatherRequest)
        }
    }

    private suspend fun processGeocodeByCityRequest(
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

                val weatherInfo = processCityWeather(lat, lon, cityWeatherRequest.units)
                if (weatherInfo == null) {
                    updateWeatherSectionState(WeatherSectionState.SearchError("Error fetching weather"))
                    return
                }

                updateWeatherSectionState(
                    WeatherSectionState.SearchSuccess(weatherInfo)
                )
                ModelUtils.saveSearchFormStateToPreferences(preferences, searchPageState.value.searchFormState)
            }
        }
    }

    private suspend fun processCityWeather(lat: Double, lon: Double, units: Units): CityWeatherInfo? {

        val cityWeatherInput = GetCityWeatherInput(
            apiKey = OpenWeatherApiManager.API_KEY,
            lat = lat.toString(),
            lon = lon.toString(),
            units = units.apiValue
        )

        return when (val cityWeatherOutput = getCityWeatherUseCase.execute(cityWeatherInput)) {
            is UseCaseResult.Error -> {
                return null
            }

            is UseCaseResult.Success -> {
                cityWeatherOutput.value.cityWeather
            }
        }
    }

    private fun tryCurrentLocation(context: Context, permission: String) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            updateShowPermissionState(true)
        } else {
            presentCurrentLocationWeather(context)
        }
    }

    fun locationPermissionResult(context: Context, granted: Boolean) {
        if (granted) {
            presentCurrentLocationWeather(context)
        }
    }

    private fun presentCurrentLocationWeather(context: Context) {
        viewModelScope.launch(dispatcherProvider.main) {
            updateWeatherSectionState(WeatherSectionState.Loading)
            val location = getLocation(context)
            if (location != null) {
                val weatherInfo = processCityWeather(
                    location.latitude,
                    location.longitude,
                    searchPageState.value.searchFormState.units
                )
                if (weatherInfo == null) {
                    updateWeatherSectionState(WeatherSectionState.SearchError("Error fetching weather"))
                    return@launch
                }

                //updateWeatherSectionState(WeatherSectionState.SearchSuccess(weatherInfo))
                //updateSearchFormState(SearchFormState(citySearched = weatherInfo.name ?: "-"))
                _searchPageState.value = SearchPageState(
                    searchFormState = SearchFormState(citySearched = weatherInfo.name ?: "-"),
                    weatherSectionState = WeatherSectionState.SearchSuccess(weatherInfo)
                )
                ModelUtils.saveSearchFormStateToPreferences(preferences, searchPageState.value.searchFormState)

            } else {
                updateWeatherSectionState(
                    WeatherSectionState.SearchError(
                        "Location Permission is granted but there were some issues getting your location"
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLocation(context: Context): Location? {
        return try {
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            val task = locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            )
            task.await()
        } catch (th: Throwable) {
            null
        }
    }

    fun clearShouldAskLocationPermission() {
        updateShowPermissionState(false)
    }

}
