# MuZafrik 🎵

Lecteur de musique Android moderne à l'identité visuelle africaine : ocre, terre cuite, vert profond et or,
avec une frise géométrique inspirée des tissus kente / bogolan.

- **Package** : `com.muzafrik.app`
- **Stack** : Kotlin, Jetpack Compose (Material 3), Media3 (ExoPlayer + MediaSessionService), Room, Hilt, MVVM
- **SDK** : minSdk 26 · targetSdk / compileSdk 35

## Fonctionnalités (MVP)

- Scan des musiques locales (MediaStore) avec permissions Android 13+ (`READ_MEDIA_AUDIO`) et repli `READ_EXTERNAL_STORAGE`
- Bibliothèque à onglets : Titres, Albums, Artistes, Playlists (+ écrans de détail)
- Lecteur plein écran : pochette, play/pause, précédent/suivant, progression, shuffle, répétition
- Mini-lecteur persistant en bas de l'écran
- Lecture en arrière-plan avec notification et contrôles sur l'écran de verrouillage (Media3)
- Playlists et favoris persistés (Room)
- Recherche tolérante aux accents (titres, albums, artistes)
- Thème clair / sombre (bouton dans l'en-tête de la bibliothèque)

## Captures d'écran

> À ajouter dans `docs/screenshots/` puis à référencer ici :
> `![Bibliothèque](docs/screenshots/library.png)` · `![Lecteur](docs/screenshots/player.png)`

## Architecture

```
app/src/main/java/com/muzafrik/app
├── data
│   ├── local        Room : entités, DAO, base
│   ├── media        MediaStoreDataSource (scan des fichiers audio)
│   ├── repository   MusicRepository (source unique de vérité)
│   └── settings     Préférence de thème
├── domain.model     Song, Album, Artist, Playlist
├── player           PlaybackService (MediaSessionService) + PlayerController (MediaController)
├── di               Modules Hilt
├── ui
│   ├── theme        Palette, typographie, thème M3
│   ├── components   Pochette, lignes de titres, dialogues, frise africaine
│   ├── library      Bibliothèque, écran de détail, ViewModel
│   ├── player       Lecteur plein écran, mini-lecteur, ViewModel
│   ├── search       Recherche
│   ├── permissions  Gestion des permissions
│   └── settings     ViewModel du thème
└── util             Formatage, normalisation de texte
```

## Compiler

Prérequis : JDK 17 et Android SDK (compileSdk 35). Android Studio Ladybug (2024.2) ou plus récent convient.

```bash
# 1. Générer le Gradle Wrapper (une seule fois, nécessite Gradle 8.9+ installé)
gradle wrapper --gradle-version 8.9

# 2. Compiler l'APK debug
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

Ou ouvrir le dossier dans Android Studio : la synchronisation Gradle génère le wrapper automatiquement.
Pensez à committer `gradlew`, `gradlew.bat` et `gradle/wrapper/gradle-wrapper.jar`.

## CI / GitHub Actions

- `.github/workflows/build.yml` : compile l'APK **debug** à chaque push / PR et le publie en artefact
  (`MuZafrik-debug-apk`).
- `.github/workflows/release.yml` (optionnel, manuel ou sur tag `v*`) : compile l'APK **release signé**.

Secrets GitHub à créer pour la release :

| Secret | Contenu |
|---|---|
| `KEYSTORE_BASE64` | Keystore encodé : `base64 -w0 release.jks` |
| `KEYSTORE_PASSWORD` | Mot de passe du keystore |
| `KEY_ALIAS` | Alias de la clé |
| `KEY_PASSWORD` | Mot de passe de la clé |

Créer un keystore : `keytool -genkeypair -v -keystore release.jks -alias muzafrik -keyalg RSA -keysize 2048 -validity 10000`
(ne jamais le committer : `*.jks` est dans `.gitignore`).

## Idées pour la suite

Égaliseur, minuterie de sommeil, file d'attente éditable, renommage de playlist, tags/pochettes embarqués,
Android Auto, chaînes de caractères dans `strings.xml` (i18n).
