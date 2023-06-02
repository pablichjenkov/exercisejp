package io.github.pablichj.exercisejp.data

import com.google.gson.annotations.SerializedName

data class CityWeatherInfo(
    @SerializedName("coord") var coord: Coord? = Coord(),
    @SerializedName("weather") var weather: List<Weather>? = null,
    @SerializedName("base") var base: String? = null,
    @SerializedName("main") var main: Main? = Main(),
    @SerializedName("visibility") var visibility: Long? = null,
    @SerializedName("wind") var wind: Wind? = Wind(),
    @SerializedName("rain") var rain: Map<String, Double>? = null,
    @SerializedName("clouds") var clouds: Map<String, Long>? = null,
    @SerializedName("dt") var dt: Long? = null,
    @SerializedName("sys") var sys: Sys? = null,
    @SerializedName("timezone") var timezone: Int? = null,
    @SerializedName("id") var id: Long? = null,
    @SerializedName("name") var name: String? = null
)

data class Coord(
    @SerializedName("lon") var lon: Double? = null,
    @SerializedName("lat") var lat: Double? = null
)

data class Weather(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("icon") var icon: String? = null
)

data class Main(
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("feels_like") var feelsLike: Double? = null,
    @SerializedName("temp_min") var tempMin: Double? = null,
    @SerializedName("temp_max") var tempMax: Double? = null,
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("sea_level") var seaLevel: Int? = null,
    @SerializedName("grnd_level") var grndLevel: Int? = null
)

data class Wind(
    @SerializedName("speed") var speed: Double? = null,
    @SerializedName("deg") var deg: Int? = null,
    @SerializedName("gust") var gust: Double? = null
)

data class Sys (
    @SerializedName("type"    ) var type    : Int?    = null,
    @SerializedName("id"      ) var id      : Long?    = null,
    @SerializedName("country" ) var country : String? = null,
    @SerializedName("sunrise" ) var sunrise : Int?    = null,
    @SerializedName("sunset"  ) var sunset  : Int?    = null
)