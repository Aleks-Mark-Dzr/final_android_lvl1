package com.example.skillcinema.data

data class Actor(
    val id: Int,
    val name: String,
    val role: String,
    val photoUrl: String?,
    val profession: String?,
    /** "MALE" / "FEMALE" из api/v1/staff/{id}; у актёров из состава фильма неизвестен. */
    val gender: String? = null
) {
    val isFemale: Boolean get() = gender.equals("FEMALE", ignoreCase = true)
}
