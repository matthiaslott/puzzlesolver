# PuzzleSolver

Heuristics-based Sudoku solver written in Kotlin using Compose Multiplatform.
It assists users when solving Sudokus by suggesting reasoning steps for progressing the game.
The suggestions are visualised on the board to foster understanding of the techniques.

<p align="center">
  <img src="illustrations/hidden_single.png" width="24%"/>
  <img src="illustrations/intersection.png" width="24%"/>
  <img src="illustrations/solved.png" width="24%"/>
  <img src="illustrations/video.gif" width="24%"/>
</p>

Currently Supported Heuristics:
- [Naked Single](https://sudokubliss.com/guides/naked-singles-technique),
  [Naked Pair](https://sudokubliss.com/guides/naked-pairs-triples-quads) and
  [Naked Triple](https://sudokubliss.com/guides/naked-pairs-triples-quads)
- [Hidden Single](https://sudokubliss.com/guides/hidden-singles-technique),
  [Hidden Pair](https://sudokubliss.com/guides/hidden-pairs-technique) and
  [Hidden Triple](https://sudokubliss.com/guides/hidden-triples)
- [Intersection](https://sudokubliss.com/guides/locked-candidates-or-box-column-row-interactions)


## Compose Multiplatform Information

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- Desktop tests: `./gradlew :shared:jvmTest`
- Web tests:
  - Wasm target: `./gradlew :shared:wasmJsTest`
  - JS target: `./gradlew :shared:jsTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).