package com.example.testassessment.technicalassignment.domain.repository

import com.example.testassessment.technicalassignment.domain.model.Airline
import com.example.testassessment.technicalassignment.utils.Result
import kotlinx.coroutines.flow.Flow

interface AirlineRepository {
    fun getAirlines(): Flow<Result<List<Airline>>>
}
