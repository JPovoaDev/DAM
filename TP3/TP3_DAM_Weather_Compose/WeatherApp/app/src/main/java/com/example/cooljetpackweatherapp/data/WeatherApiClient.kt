package com . example . cooljetpackweatherapp . data


object WeatherApiClient {
    private val client = HttpClient {
        install ( ContentNegotiation ) {
            json ( Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }) // Ignores extra JSON fields
        }
    }
    suspend fun getWeather (lat : Float , lon : Float ) : WeatherData ? {
        val reqString = buildString {
            append (" https :// api .open - meteo .com /v1/ forecast ?")
            append (" latitude =${lat }& longitude =${lon }&")
            append (" current_weather = true &")
            append (" hourly = temperature_2m , weathercode , pressure_msl , windspeed_10m ")
        }
        System .out . println (" Getting URL : $reqString ")
        return try {
            client .get ( reqString ). body () // Parses JSON into WeatherData
        } catch (e: Exception ) {
            e. printStackTrace ()
            null
        }
    }
}