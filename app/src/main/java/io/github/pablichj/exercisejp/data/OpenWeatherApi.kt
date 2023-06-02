package io.github.pablichj.exercisejp.data

import io.github.pablichj.exercisejp.domain.CityWeatherInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    //?q=miami,FL,US&limit=1&appid=bf07442a7f7d8d494efec85441621bb2
    @GET("${OpenWeatherApiManager.BASE_URL}/geo/1.0/direct")
    suspend fun geocodeByCityNameV1(
        @Query("appid") apiKey: String,
        @Query("q") cityStateCountryJoin: String,
        @Query("limit") limit: Int = 1
    ): Response<List<GeocodeEntry>>

    //?lat={lat}&lon={lon}&appid={API key}
    @GET("${OpenWeatherApiManager.BASE_URL}/data/2.5/weather")
    suspend fun getWeatherV_2_5(
        @Query("appid") apiKey: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String
    ): Response<CityWeatherInfo>
}