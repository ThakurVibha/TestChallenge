package com.example.testassessment.technicalassignment.domain.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Airline(
    val id: String,
    val name: String,
    val country: String,
    val headquarters: String,
    @Json(name = "fleet_size")
    val fleetSize: Int,
    val website: String,
    @Json(name = "logo_url")
    val logoUrl: String
) : Parcelable