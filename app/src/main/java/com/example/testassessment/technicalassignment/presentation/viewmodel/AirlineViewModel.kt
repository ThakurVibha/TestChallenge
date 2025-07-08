package com.example.testassessment.technicalassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testassessment.technicalassignment.domain.model.Airline
import com.example.testassessment.technicalassignment.domain.usecase.GetAirlinesUseCase
import com.example.testassessment.technicalassignment.utils.ConnectivityObserver
import com.example.testassessment.technicalassignment.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that handles airline data fetching and connectivity status using Hilt and StateFlow.
 */
@HiltViewModel
class AirlineViewModel @Inject constructor(
    private val getAirlinesUseCase: GetAirlinesUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val data: List<Airline>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    private val _airlines = MutableStateFlow<UiState>(UiState.Loading)
    val airlines: StateFlow<UiState> = _airlines

    private var hasFetched = false // 🟢 Prevent multiple fetches on unstable internet

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                val offline = status != ConnectivityObserver.Status.Available
                _isOffline.value = offline

                if (offline) {
                    _airlines.value = UiState.Error("No internet connection")
                    hasFetched = false
                } else {
                    // Fetch data only if not already fetched
                    if (!hasFetched) {
                        getAirlines()
                    }
                }
            }
        }
    }

    private fun getAirlines() {
        viewModelScope.launch {
            _airlines.value = UiState.Loading
            delay(1000L) // Optional fake loading delay for better UX

            getAirlinesUseCase().collectLatest { result ->
                _airlines.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> {
                        hasFetched = true
                        UiState.Success(result.data)
                    }
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }
}
