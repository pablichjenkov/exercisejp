package io.github.pablichj.exercisejp.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import io.github.pablichj.exercisejp.ui.theme.ExerciseJPTheme

@Composable
internal fun SearchPage(
    modifier: Modifier = Modifier,
    viewModel: SearchPageViewModel
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.locationPermissionResult(context, isGranted)
    }

    val pageState = remember { viewModel.searchPageState }

    SearchPageContent(
        modifier = modifier,
        pageState = pageState.value,
        onSubmitClick = {
            viewModel.doCityGeocodeRequest(it)
        }
    )
    if (pageState.value.shouldAskLocationPermission) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        viewModel.clearShouldAskLocationPermission()
    }
    LaunchedEffect(key1 = viewModel) {
        viewModel.start(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@Composable
internal fun SearchPageContent(
    modifier: Modifier = Modifier,
    pageState: SearchPageState,
    onSubmitClick: (CityWeatherRequest) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("SearchPageContentRootNode")
    ) {
        CityWeatherSearchForm(
            modifier,
            pageState.searchFormState,
            onSubmitClick
        )
        CityWeatherInfoView(
            pageState.weatherSectionState,
            pageState.searchFormState.units.readerValue
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseJPPreview() {
    ExerciseJPTheme {
        SearchPageContent(
            modifier = Modifier,
            pageState = SearchPageState(),
            onSubmitClick = {}
        )
    }
}
