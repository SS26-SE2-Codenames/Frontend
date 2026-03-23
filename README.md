# Frontend
A Android App Frontend for our Codenames Game written in Kotlin.

How to use the folders:

/ui/: 
everything Jetpack Compose
screens/ are whole pages, e.g. Lobby, InGame
components/ is reusable UI like Buttons or complex components, that are not a whole page

/viewmodel/:
connects UI and Logic
game state is here
use StateFlow

/network/:
holds connection to server

/data/:
all data classes that are used & transmitted & serialized to JSON

/util:
things that we might need, like helper functions etc.


Basic Rules for Android Programming:

- No network calls through UI!
- ViewModel is everything that has to do with Logic
- Data is only transferred through repositories in /data folder
- Navigation is implemented via Jetpack Navigation (as shown in /ui/navigation)
- Do not use Activities or use them wisely, switching between activities can be unnecessary difficult

This project has ktlint included. That means, Code is formatted & cleaned up automatically with this command: 
```bash
./gradlew ktlintCheck
```
for checking the code and
```bash
./gradlew ktlintFormat
```
for formatting the code. Please do this before every commit to ensure code quality.

