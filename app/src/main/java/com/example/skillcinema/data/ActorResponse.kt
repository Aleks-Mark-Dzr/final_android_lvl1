package com.example.skillcinema.data

import com.google.gson.annotations.SerializedName

data class ActorResponse(
    @SerializedName("staffId")
    val staffId: Int,

    @SerializedName("nameRu")
    val nameRu: String?,

    @SerializedName("nameEn")
    val nameEn: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("posterUrl")
    val posterUrl: String?,

    @SerializedName("professionKey")
    val professionKey: String?,

    @SerializedName("profession")
    val profession: String? = null
)