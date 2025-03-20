package com.example.skillcinema.data

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("items") val items: List<Movie>,
    @SerializedName("total") val total: Int
)