package io.github.pablichj.exercisejp.domain

import io.github.pablichj.exercisejp.data.GeocodeEntry
import io.github.pablichj.exercisejp.data.OpenWeatherApi
import javax.inject.Inject

class GeocodeCityUseCase @Inject constructor(
    private val openWeatherApi: OpenWeatherApi
) : MapOneUseCase <GeocodeCityInput, UseCaseResult<GeocodeCityOutput>> {

    override suspend fun execute(input: GeocodeCityInput): UseCaseResult<GeocodeCityOutput> {
        return try {
            val httpResp = openWeatherApi.geocodeByCityNameV1(
                apiKey = input.apiKey,
                cityStateCountryJoin = input.joinByComa()
            )
            if (httpResp.isSuccessful) {
                val geocodeFirstEntry = httpResp.body()?.get(0)
                    ?: return UseCaseResult.Error("Unexpected empty body")

                UseCaseResult.Success(GeocodeCityOutput(geocodeFirstEntry))
            } else {
                UseCaseResult.Error(httpResp.errorBody()?.string() ?: "no error body info")
            }
        } catch (th: Throwable) {
            UseCaseResult.Error(th.message ?: th.toString())
        }
    }

}

data class GeocodeCityInput(
    val apiKey: String,
    val city: String,
    val stateCode: String,
    val countryCode: String,
) {
    fun joinByComa(): String {
        return "$city,$stateCode,$countryCode"
    }
}

data class GeocodeCityOutput(
    val geocodeEntry: GeocodeEntry
)