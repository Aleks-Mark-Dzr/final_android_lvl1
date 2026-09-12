# 🎬 SkillCinema App

Мобильное Android-приложение в стиле КиноПоиска: позволяет просматривать фильмы, актёров, использовать фильтры, сохранять в избранное и многое другое.

> 📱 Учебный проект в рамках курса **«Android-разработчик. Уровень 1» от Skillbox**

---

## 🚀 Функциональность

- Поиск фильмов по названию
- Просмотр карточки фильма (актеры, рейтинг, описание)
- Галерея фото по категориям (со съёмок, постеры и др.)
- Список актёров и съёмочной группы
- Добавление в «Избранное», «Посмотрю позже»
- Хранение данных в локальной базе (Room)
- Навигация между экранами с сохранением состояния
- Firebase интеграция

---

## 🛠 Используемые технологии

- **Язык:** Kotlin
- **UI:** Jetpack Compose, XML layouts
- **Архитектура:** MVVM
- **Навигация:** Jetpack Navigation
- **Сеть:** Retrofit, REST API (https://kinopoiskapiunofficial.tech)
- **База данных:** Room, SQLite
- **Асинхронность:** Coroutines, Flow
- **DI:** Dagger/Hilt
- **Прочее:** Firebase, Glide, RecyclerView

---

## 🖼️ Скриншоты

### Первый ряд

| Главный экран | Фильмы выбранного раздела | Карточка фильма | Список актеров | Над фильмом работали | 
|---|---|---|---|---|
| <img src="screenshots/MainScreen.png" width="150"> | <img src="screenshots/все фильмы раздела.png" width="150"> | <img src="screenshots/Детализация фильма.png" width="150"><br><img src="screenshots/Детализация фильма 2.png" width="150"></div> | <img src="screenshots/Список актеров.png" width="150"> | <img src="screenshots/Над фильмом работали.png" width="150"> |

### Второй ряд
| Галерея | Страница актера | Фильмография | Профиль | Коллекция Любимые |
|---|---|---|---|---|
| <img src="screenshots/Галерея фильма.png" width="150"> | <img src="screenshots/Профиль актера.png" width="150"> | <img src="screenshots/Фильмография.png" width="150"> | <img src="screenshots/Профиль_.png" width="150"> | <img src="screenshots/Коллекция Любимые.png" width="150"> |

### Третий ряд
| Экран поиска | Фильтр поиска |
|---|---|
| <img src="screenshots/Поиск.png" width="150"> | <img src="screenshots/Фильтр поиска.png" width="150"> |
---

## ⚙️ Установка

1. Клонируйте проект:
   ```bash
   git clone https://github.com/Aleks-Mark-Dzr/final_android_lvl1.git
