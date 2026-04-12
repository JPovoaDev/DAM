package com.example.dogviewer.network

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) capturing the raw JSON structure of the Dog API.
 * The `message` field corresponds to the actual image URL we will map to ImageItem.
 */
data class DogApiResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("status")
    val status: String
)
