package com.example.testassessment.technicalassignment.domain.usecase

import com.example.testassessment.technicalassignment.domain.model.Airline
import com.example.testassessment.technicalassignment.domain.repository.AirlineRepository
import com.example.testassessment.technicalassignment.utils.Result
import kotlinx.coroutines.flow.Flow

class GetAirlinesUseCase(
    private val repository: AirlineRepository
) {
    operator fun invoke(): Flow<Result<List<Airline>>> {
        return repository.getAirlines()
    }
}
