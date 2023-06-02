package io.github.pablichj.exercisejp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun CityWeatherView(
    weatherState: WeatherSectionState,
    units: String
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (weatherState) {
            WeatherSectionState.Empty -> {
                Text(text = "Fill the form to look up for the weather in that city. Only available in the US ")
            }

            WeatherSectionState.Loading -> {
                CircularProgressIndicator()
            }

            is WeatherSectionState.SearchError -> {
                Text(text = weatherState.error)
            }

            is WeatherSectionState.SearchSuccess -> {
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = weatherState.weatherInfo.name ?: "city name empty")
                    Text(
                        text = ModelUtils.buildCityWeatherInfo(weatherState.weatherInfo, units)
                    )
                }
            }
        }
    }
}