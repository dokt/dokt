# Contributing

## Release process

1. Check dependency updates by running `dependencyUpdates` tasks.
2. Check fixed versions:
   1. `settings.gradle.kts` `de.fayard.refreshVersions`
   2. `build.gradle.kts`:
      1. `subprojects.version`
      2. `tasks.wrapper.gradleVersion` 
   3. `app.dokt.gradle.Versions.kt`
3. Check that there isn't failing tests by running `allTests` tasks.
4. Do manual testing
5. Remove `-SNAPSHOT` from versions.
6. Write release notes.
7. Optionally test using Sonatype repositories by running `publishToSonatype` tasks. 
8. Publish all artifacts (including plugin) to Maven central by running `publishToSonatype closeAndReleaseSonatypeStagingRepository` tasks (in same run).
9. Publish plugin to Gradle plugin portal by running `:dokt-gradle:publishPlugins` task.
10. Commit changes.
11. Create tag of the release.

## Links

- [Gradle Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)
- [Kotlin Multiplatform libraries](https://libs.kmp.icerock.dev/)
  - Add Dokt there
- [Yet another kotlin multiplatform libraries](https://github.com/AAkira/Kotlin-Multiplatform-Libraries)

### DDD links

- [Domain vs. App Service](https://stackoverflow.com/questions/2268699/domain-driven-design-domain-service-application-service)

## TODO

- [Idea doesn't recognize Kotlin Common Maven dependencies](https://youtrack.jetbrains.com/issue/IDEA-296313/Idea-doesnt-recognize-Kotlin-Common-Maven-dependencies)
  - [Configure compilations](https://kotlinlang.org/docs/multiplatform-configure-compilations.html)
- Dependency Injection
  1. Generate automatically
  2. [Dagger](https://dagger.dev/) is fast
  3. [Koin](https://insert-koin.io/) is famous and has Ktor support
  4. [Kodein](https://docs.kodein.org/kodein-framework/index.html) yet another DI framework
- Use [Clikt](https://github.com/ajalt/clikt) in CLI
- Use Dokka