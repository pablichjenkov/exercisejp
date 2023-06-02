package io.github.pablichj.exercisejp.domain

import io.github.pablichj.exercisejp.data.OpenWeatherApi
import javax.inject.Inject

class GetCityWeatherUseCase @Inject constructor(
    private val openWeatherApi: OpenWeatherApi
) : MapOneUseCase <GetCityWeatherInput, UseCaseResult<GetCityWeatherOutput>> {

    override suspend fun execute(input: GetCityWeatherInput): UseCaseResult<GetCityWeatherOutput> {
        return try {
            val httpResp = openWeatherApi.getWeatherV_2_5(
                apiKey = input.apiKey,
                lat = input.lat,
                lon =input.lon,
                units = input.units
            )
            if (httpResp.isSuccessful) {
                val cityWeatherInfo = httpResp.body()
                    ?: return UseCaseResult.Error("Unexpected empty body")

                UseCaseResult.Success(GetCityWeatherOutput(cityWeatherInfo))
            } else {
                UseCaseResult.Error(httpResp.errorBody()?.string() ?: "no error body info")
            }
        } catch (th: Throwable) {
            UseCaseResult.Error(th.message ?: th.toString())
        }
    }


}

data class GetCityWeatherInput(
    val apiKey: String,
    val lat: String,
    val lon: String,
    val units: String
)

data class GetCityWeatherOutput(
    val cityWeather: CityWeatherInfo
)
