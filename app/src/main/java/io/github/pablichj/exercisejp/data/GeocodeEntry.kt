package io.github.pablichj.exercisejp.data

data class GeocodeEntry(
    val name: String? = null,
    val local_names: Map<String, String>? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val country: String? = null,
    val state: String? = null
)