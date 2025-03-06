package com.example.skillcinema.data

import com.google.gson.annotations.SerializedName

data class ActorDetailsResponse(
    @SerializedName("personId") val id: Int,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("sex") val gender: String?,
    @SerializedName("posterUrl") val photoUrl: String?,
    @SerializedName("growth") val height: Int?,
    @SerializedName("birthday") val birthDate: String?,
    @SerializedName("death") val deathDate: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("birthplace") val birthPlace: String?,
    @SerializedName("deathplace") val deathPlace: String?,
    @SerializedName("hasAwards") val hasAwards: Int?,
    @SerializedName("profession") val profession: String?,
    @SerializedName("facts") val facts: List<String>?,
    @SerializedName("films") val filmography: List<ActorFilm>
) {
    data class ActorFilm(
        @SerializedName("filmId") val id: Int,
        @SerializedName("nameRu") val titleRu: String?,
        @SerializedName("nameEn") val titleEn: String?,
        @SerializedName("rating") val rating: String?,
        @SerializedName("general") val isMainRole: Boolean,
        @SerializedName("description") val role: String?
    )
}