# Contributing

## Release process

1. Check dependency updates by running `dependencyUpdates` tasks.
2. Check that there isn't failing tests by running `allTests` tasks.
3. Do manual testing
4. Remove `-SNAPSHOT` from versions.
5. Write release notes.
6. Optionally test using Sonatype repositories by running `publishToSonatype` tasks. 
7. Publish all artifacts (including plugin) to Maven central by running `publishToSonatype closeAndReleaseSonatypeStagingRepository` tasks (in same run).
8. Publish plugin to Gradle plugin portal by running `:dokt-gradle:publishPlugins` task.
9. Commit changes.
10. Create tag of the release.

## Links

- [Gradle Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)
- [Kotlin Multiplatform libraries](https://libs.kmp.icerock.dev/)
  - Add Dokt there
- [Yet another kotlin multiplatform libraries](https://github.com/AAkira/Kotlin-Multiplatform-Libraries)

## TODO

- [Idea doesn't recognize Kotlin Common Maven dependencies](https://youtrack.jetbrains.com/issue/IDEA-296313/Idea-doesnt-recognize-Kotlin-Common-Maven-dependencies)
  - [Configure compilations](https://kotlinlang.org/docs/multiplatform-configure-compilations.html)
- Dependency Injection
  1. Generate automatically
  1. [Dagger](https://dagger.dev/) is fast
  1. [Koin](https://insert-koin.io/) is famous and has Ktor support
  1. [Kodein](https://docs.kodein.org/kodein-framework/index.html) yet another DI framework
- Use [Clikt](https://github.com/ajalt/clikt) in CLI
- Use Dokka