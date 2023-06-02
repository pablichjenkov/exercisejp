package io.github.pablichj.exercisejp

import android.content.SharedPreferences
import io.github.pablichj.exercisejp.data.GeocodeEntry
import io.github.pablichj.exercisejp.domain.GeocodeCityOutput
import io.github.pablichj.exercisejp.domain.GeocodeCityUseCase
import io.github.pablichj.exercisejp.domain.GetCityWeatherUseCase
import io.github.pablichj.exercisejp.domain.UseCaseResult
import io.github.pablichj.exercisejp.ui.CityWeatherRequest
import io.github.pablichj.exercisejp.ui.SearchPageViewModel
import io.github.pablichj.exercisejp.ui.Units
import io.github.pablichj.exercisejp.ui.WeatherSectionState
import io.github.pablichj.exercisejp.util.DispatcherProvider
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SearchPageViewModelTest {
    @Test
    fun `A new instance of ViewModel should make a geocode request with the previous saved city`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val dispatcherProvider = DispatcherProvider(main = testDispatcher)

        val preferences = mock<SharedPreferences>()

        val geoCodeUseCase = mock<GeocodeCityUseCase>()
        whenever(geoCodeUseCase.execute(any())).thenReturn (
            UseCaseResult.Success(
                GeocodeCityOutput(
                    GeocodeEntry()
                )
            )
        )

        val cityWeatherUseCase = mock<GetCityWeatherUseCase>()

        val viewModel = SearchPageViewModel(
            dispatcherProvider = dispatcherProvider,
            preferences = preferences,
            geocodeCityUseCase =  geoCodeUseCase,
            getCityWeatherUseCase = cityWeatherUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()
        verify(geoCodeUseCase, times(1)).execute(any())
    }

    @Test
    fun `ViewModel makes a geocode request when doCityGeocodeRequest is called`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val dispatcherProvider = DispatcherProvider(main = testDispatcher)
        val preferences = mock<SharedPreferences>()

        val geoCodeUseCase = mock<GeocodeCityUseCase>()
        whenever(geoCodeUseCase.execute(any())).thenReturn (
            UseCaseResult.Error(
                "An error happen"
            )
        )

        val cityWeatherUseCase = mock<GetCityWeatherUseCase>()

        val viewModel = SearchPageViewModel(
            dispatcherProvider = dispatcherProvider,
            preferences = preferences,
            geocodeCityUseCase =  geoCodeUseCase,
            getCityWeatherUseCase = cityWeatherUseCase
        )

        val request = CityWeatherRequest(
            city = "Albany",
            stateCode = "NY",
            countryCode = "US",
            Units.Celsius
        )
        println("Pablo 0 -" + viewModel.searchPageState.value)
        viewModel.doCityGeocodeRequest(request)
        println("Pablo 1 -" + viewModel.searchPageState.value)
        testDispatcher.scheduler.advanceUntilIdle()

        val weatherStateIsError =
            viewModel.searchPageState.value.weatherSectionState is WeatherSectionState.SearchError

        assertTrue(weatherStateIsError)

        // We check for 2 times because the initial call when the ViewModel is created is also counted
        verify(geoCodeUseCase, times(2)).execute(any())
    }

}