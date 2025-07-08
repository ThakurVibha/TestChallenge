package com.example.testassessment.technicalassignment.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.testassessment.R
import com.example.testassessment.technicalassignment.domain.model.Airline
import com.example.testassessment.technicalassignment.presentation.viewmodel.AirlineViewModel


/**
 * Displays a list of airlines with loading, error, and offline handling states.
 *
 * Shows:
 * - Alert dialog if offline
 * - Airline cards in a colorful list on success
 * - Error message on failure
 *
 * @param viewModel ViewModel providing airline data and connectivity status
 * @param onItemClick Callback triggered when a user selects an airline
 */

@Composable
fun AirlineListScreen(
    viewModel: AirlineViewModel,
    onItemClick: (Airline) -> Unit
) {
    val state by viewModel.airlines.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var showDialog by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    // Trigger dialog on network loss
    LaunchedEffect(isOffline) {
        if (isOffline) showDialog = true
    }

    // Show internet dialog
    if (isOffline && showDialog) {
        NoInternetDialog(onDismiss = { showDialog = false })
    }

    // 🔴 Show full screen "no internet" view instead of data
    if (isOffline) {
        FullScreenOfflineMessage()
        return
    }

    when (val uiState = state) {
        is AirlineViewModel.UiState.Loading -> LoadingScreen()
        is AirlineViewModel.UiState.Success -> {
            val allAirlines = uiState.data
            val filtered = allAirlines.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.country.contains(searchQuery, ignoreCase = true)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                AirlineSearchBar(searchQuery) { searchQuery = it }

                if (filtered.isEmpty()) {
                    EmptyStateMessage()
                } else {
                    AirlineList(airlines = filtered, onItemClick = onItemClick)
                }
            }
        }
        is AirlineViewModel.UiState.Error -> ErrorScreen(uiState.message)
        else -> {}
    }
}


@Composable
fun NoInternetDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = Color.Black)
            }
        },
        title = {
            Text("No Internet", color = Color.Black, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "You're currently offline. Please check your internet connection.",
                color = Color.DarkGray
            )
        },
        containerColor = Color.White
    )
}

@Composable
fun FullScreenOfflineMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.no_internet),
                contentDescription = "No Internet",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Internet not available",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Please connect to the internet to continue.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
            )
        }
    }
}

@Composable
fun AirlineSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search airlines by name or country") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "No airlines found.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun AirlineList(airlines: List<Airline>, onItemClick: (Airline) -> Unit) {
    val cardColors = listOf(
        Color(0xFFFFCDD2),
        Color(0xFFBBDEFB),
        Color(0xFFFFF9C4),
        Color(0xFFC8E6C9),
        Color(0xFFD1C4E9)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        itemsIndexed(airlines) { index, airline ->
            AirlineCardItem(
                airline = airline,
                backgroundColor = cardColors[index % cardColors.size],
                onClick = { onItemClick(airline) }
            )
        }
    }
}

@Composable
fun AirlineCardItem(
    airline: Airline,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(airline.logoUrl),
                contentDescription = airline.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = airline.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Country: ${airline.country}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Text(
                text = "${airline.fleetSize} ✈️",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}