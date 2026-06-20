# RAWG Games

Android app browsing games from the [RAWG Video Games Database API](https://rawg.io/apidocs), built with Kotlin & Jetpack Compose.

## Features

- Paginated game list with endless scroll
- Search games by name
- Game detail screen with description, rating, metacritic score
- Local favorites using Room database

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (ViewModel + Repository)
- **Networking:** Retrofit 2 + OkHttp 4 + Gson
- **Local Storage:** Room Database
- **Image Loading:** Coil
- **DI:** Manual factory pattern

## Setup

1. Get a free API key at [rawg.io/apidocs](https://rawg.io/apidocs)
2. Open `local.properties` and set your key:
   ```
   RAWG_API_KEY=your_api_key_here
   ```
3. Build & run with Android Studio

## Project Structure

```
app/src/main/java/com/cancreative/rawggames/
├── MainActivity.kt
├── data/
│   ├── local/          # Room DB, DAO, entities
│   ├── remote/         # Retrofit service & response models
│   └── repository/     # GameRepository
└── ui/
    ├── theme/          # Color, Theme, Typography
    ├── view/           # Compose screens
    └── viewmodel/      # GameViewModel
```
