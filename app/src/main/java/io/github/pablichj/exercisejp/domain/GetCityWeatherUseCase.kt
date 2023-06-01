package io.github.pablichj.exercisejp.domain

import javax.inject.Inject

class GetCityWeatherUseCase @Inject constructor() : MapOneUseCase <SearchCityReq, SearchCityResp> {
    override suspend fun execute(input: SearchCityReq): SearchCityResp {
        TODO("Not yet implemented")
    }

}

data class SearchCityReq(
    val city: String,
    val stateCode: String,
    val countryCode: String,
)

data class SearchCityResp(
    val city: String,
    val stateCode: String,
    val countryCode: String,
)