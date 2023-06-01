package io.github.pablichj.exercisejp.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class EmptySearchPageViewModel : SearchPageViewModel {

    override val searchPageState: State<SearchPageState>
        get() = mutableStateOf(SearchPageState.Idle)

    override fun start() {
    }

    override fun doCityGeocodeRequest(
        city: String,
        stateCode: String,
        countryCode: String
    ) {

    }
}