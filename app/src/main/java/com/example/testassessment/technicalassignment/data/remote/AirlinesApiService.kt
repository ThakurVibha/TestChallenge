package com.example.testassessment.technicalassignment.data.remote

import com.example.testassessment.technicalassignment.domain.model.Airline
import retrofit2.http.GET

interface AirlinesApiService {
    @GET("airlines.json")
    suspend fun getAirlines(): List<Airline>
}
