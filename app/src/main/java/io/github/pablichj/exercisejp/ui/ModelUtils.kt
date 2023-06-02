package io.github.pablichj.exercisejp.ui

import android.content.SharedPreferences
import io.github.pablichj.exercisejp.data.CityWeatherInfo
import java.lang.StringBuilder

object ModelUtils {

    const val CityPrefKey = "CityPrefKey"
    const val StatePrefKey = "StatePrefKey"
    const val CountryPrefKey = "CountryPrefKey"

    fun buildCityWeatherInfo(weatherInfo: CityWeatherInfo, units: String): String {
        val mainWeather = weatherInfo.main
        val wind = weatherInfo.wind
        return StringBuilder()
            .appendLine("Main Info:")
            .appendLine("    Temp Max: ${mainWeather?.tempMax ?: "-"} $units")
            .appendLine("    Temp Min: ${mainWeather?.tempMin ?: "-"} $units")
            .appendLine("    Temp Average: ${mainWeather?.temp ?: "-"} $units")
            .appendLine("    Temp Feels Like: ${mainWeather?.feelsLike ?: "-"} $units")
            .appendLine("    Humidity: ${mainWeather?.humidity ?: "-"}")
            .appendLine("    Pressure: ${mainWeather?.pressure ?: "-"}")
            .appendLine("Wind Info:")
            .appendLine("    Speed: ${wind?.speed ?: "-"}")
            .appendLine("    Gust: ${wind?.gust ?: "-"}")
            .toString()
    }

    fun saveSearchFormStateToPreferences(
        preferences: SharedPreferences,
        searchFormState: SearchFormState
    ): SearchFormState {
        with (preferences.edit()) {
            putString(CityPrefKey, searchFormState.citySearched)
            putString(StatePrefKey, searchFormState.stateSearched)
            putString(CountryPrefKey, searchFormState.countrySearched)
            commit()
        }
        return SearchFormState(
            citySearched = preferences.getString(CityPrefKey, "").orEmpty(),
            stateSearched = preferences.getString(StatePrefKey, "").orEmpty(),
            countrySearched = preferences.getString(CountryPrefKey, "").orEmpty(),
        )
    }

    fun createSearchFormStateFromPreferences(
        preferences: SharedPreferences
    ): SearchFormState {
        return SearchFormState(
            citySearched = preferences.getString(CityPrefKey, "").orEmpty(),
            stateSearched = preferences.getString(StatePrefKey, "").orEmpty(),
            countrySearched = preferences.getString(CountryPrefKey, "").orEmpty(),
        )
    }

}