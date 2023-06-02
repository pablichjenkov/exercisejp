package io.github.pablichj.exercisejp.ui

import io.github.pablichj.exercisejp.data.CityWeatherInfo

data class SearchPageState(
    var searchFormState: SearchFormState = SearchFormState(),
    var weatherSectionState: WeatherSectionState = WeatherSectionState.Empty,
    var shouldAskLocationPermission: Boolean = false
)

data class SearchFormState(
    var citySearched: String = "",
    var stateSearched: String = "",
    var countrySearched: String = "US",
    var units: Units = Units.Fahrenheit,
    var selectedIndex: Int = 0
)

sealed class WeatherSectionState {
    object Empty : WeatherSectionState()
    object Loading : WeatherSectionState()
    class SearchSuccess(val weatherInfo: CityWeatherInfo) : WeatherSectionState()
    class SearchError(val error: String) : WeatherSectionState()
}

enum class Units(val apiValue: String, val readerValue: String) {
    Fahrenheit("imperial", "F"),
    Celsius("metric", "C"),
    Kelvin("standard", "K")
}
