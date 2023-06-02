package io.github.pablichj.exercisejp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.pablichj.exercisejp.data.Weather

@Composable
internal fun CityWeatherInfoView(
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
                    val weather  = weatherState.weatherInfo.weather?.getOrNull(0)
                    if (weather != null) {
                        WeatherHeadline(
                            city = weatherState.weatherInfo.name ?: "-",
                            weather = weather
                        )
                    }
                    Text(
                        text = ModelUtils.buildCityWeatherInfo(weatherState.weatherInfo, units)
                    )
                }
            }
        }
    }
}

@Composable
internal fun WeatherHeadline(
    modifier: Modifier= Modifier,
    city: String,
    weather: Weather
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "$city: ${weather.description}")
            AsyncImage(
                modifier = Modifier.requiredSize(48.dp),
                model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
                contentDescription = null,
            )
        }
    }
}