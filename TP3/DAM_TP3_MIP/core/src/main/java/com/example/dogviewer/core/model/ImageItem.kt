package com.example.dogviewer.core.model

import java.util.UUID

/**
 * Represents a single Dog Image entity.
 * As defined in 04_data_model.md, it maps to the Dog API response.
 * The API only provides a URL, so we generate a unique ID by default.
 */
data class ImageItem(
    val url: String,
    val title: String,
    val breed: String,
    val subBreed: String? = null,
    val id: String = UUID.randomUUID().toString()
)
