package com.example.skillcinema.domain.models

/**
 * Профессии, которые приходят в поле `professionKey` фильмографии (api/v1/staff/{id}).
 * Порядок объявления определяет порядок чипов на экране фильмографии.
 */
enum class Profession(
    val key: String,
    private val maleTitle: String,
    private val femaleTitle: String = maleTitle
) {
    ACTOR("ACTOR", "Актер", "Актриса"),
    VOICE("VOICE", "Актер дубляжа", "Актриса дубляжа"),
    HIMSELF("HIMSELF", "Актер: играет самого себя"),
    HERSELF("HERSELF", "Актриса: играет саму себя"),
    DIRECTOR("DIRECTOR", "Режиссер"),
    WRITER("WRITER", "Сценарист"),
    PRODUCER("PRODUCER", "Продюсер"),
    PRODUCER_USSR("PRODUCER_USSR", "Директор фильма"),
    OPERATOR("OPERATOR", "Оператор"),
    COMPOSER("COMPOSER", "Композитор"),
    DESIGN("DESIGN", "Художник"),
    EDITOR("EDITOR", "Монтажер"),
    VOICE_DIRECTOR("VOICE_DIRECTOR", "Режиссер дубляжа"),
    TRANSLATOR("TRANSLATOR", "Переводчик"),
    OTHER("OTHER", "Другие роли");

    fun titleFor(isFemale: Boolean): String = if (isFemale) femaleTitle else maleTitle

    companion object {
        fun fromKey(key: String?): Profession {
            val normalized = key?.trim()?.uppercase() ?: return OTHER
            return entries.firstOrNull { it.key == normalized } ?: OTHER
        }
    }
}
