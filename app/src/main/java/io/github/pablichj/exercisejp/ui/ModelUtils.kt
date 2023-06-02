package io.github.pablichj.exercisejp.ui

import io.github.pablichj.exercisejp.data.CityWeatherInfo
import java.lang.StringBuilder

object ModelUtils {

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

}