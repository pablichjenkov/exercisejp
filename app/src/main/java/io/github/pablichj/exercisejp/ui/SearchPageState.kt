package io.github.pablichj.exercisejp.ui

import io.github.pablichj.exercisejp.data.GeocodeEntry

sealed class SearchPageState {

    var lastCitySearched = ""

    object Idle: SearchPageState()
    object Loading: SearchPageState()
    class SearchSuccess(val geocodeEntry: GeocodeEntry): SearchPageState()
    class SearchError(val error: String): SearchPageState()
}