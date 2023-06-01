package io.github.pablichj.exercisejp.ui

import androidx.compose.runtime.State

interface SearchPageViewModel {
    val searchPageState: State<SearchPageState>
    fun start()
    fun doCityGeocodeRequest(city: String, stateCode: String, countryCode: String)
}