package io.github.pablichj.exercisejp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.pablichj.exercisejp.ui.theme.ExerciseJPTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchPage(
    modifier: Modifier = Modifier,
    viewModel: SearchPageViewModel
) {
    val pageState by viewModel.searchPageState
    var cityName: String by remember { mutableStateOf(pageState.lastCitySearched) }
    var stateCode: String by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            value = cityName,
            onValueChange = {
                cityName = it
                pageState.lastCitySearched = it
            },
            label = { Text(text = "Enter City Name") }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            value = stateCode,
            onValueChange = {
                stateCode = it
            },
            label = { Text(text = "Enter State Code") }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { viewModel.doCityGeocodeRequest(cityName, stateCode, "US") }
        ) {
            Text(text = "Submit")
        }
        CityWeatherView(pageState)
    }
}

@Composable
internal fun CityWeatherView(searchPageState: SearchPageState) {
    when (searchPageState) {
        SearchPageState.Idle -> {}
        SearchPageState.Loading -> {
            CircularProgressIndicator()
        }
        is SearchPageState.SearchError -> {
            Text(text = searchPageState.error)
        }
        is SearchPageState.SearchSuccess -> {
            Text(text = searchPageState.geocodeEntry.lat.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExerciseJPTheme {
        SearchPage(viewModel = EmptySearchPageViewModel())
    }
}
