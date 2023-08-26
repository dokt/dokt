# Dokt documentation

## Features

### Dokt Gradle plugin features

#### Project detection

Dokt detects architecture layer and platform from project using following logic:

If the project:
- is root project it's only container project.
- name starts with `dom`, it's a project containing only domain logic projects for Multiplatform.
- path starts with `dom`, it's a domain logic project for Multiplatform.
- name ends with `-dom`, it's a domain logic project for Multiplatform.
- name ends with `-app`, it's an application services project for Multiplatform.
- name contains `-if`, it's application interface project:
  - if path contains `jvm`, it's for JVM platform.
  - if path contains `js`, it's for JS platform.
  - otherwise it's for Multiplatform.
- otherwise it's an infrastructure project:
  - if path contains `jvm`, it's for JVM platform.
  - if path contains `js`, it's for JS platform.
  - otherwise it's for Multiplatform.

#### For the root project

- Defines Gradle Wrapper version

#### For all projects
 
- Adds Maven Central repository

#### Activates Dokt plugin if project has `src/commonMain` directory
- Applies Kotlin Multiplatform plugin
- Applies Kotlin Serialization plugin
- Configures JVM target
- Uses JUnit Platform
- Adds Dokt Core to Common Main dependencies
- Adds Dokt Test to Common Test dependencies
- Adds generated sources to Common Main
- Adds generated sources to Common Test
- Triggers code generation before compiling Kotlin 

# TODO

- Parse Kotlin script files.
- Parse package and imports from files.
- Create Dependency generator
  - Configured in properties files e.g.:
    ```properties
    org.slf4j = api("org.slf4j:slf4j-api:_")\
                runtimeOnly("ch.qos.logback:logback-classic:_")
    org.slf4j.bridge = implementation("org.slf4j:jul-to-slf4j:_")
    ```
  - Can be configured per project
  - Can be configured via extension:
    ```kotlin
    dependencyGenerator {
      useRefreshVersionsNotation = true // defaults to false
    }
    ```
  - Dokt project plugin configures
- DoktSettingsExtensions
  ```kotlin
  dokt {
    // Store configuration in-memory (default) or in build.gradle.kts file.
    configureBuildFile = true
    // Is build.gradle.kts file updated (default) or replaced.
    replaceBuildFile = false
    // Dokt dependency generator uses refreshVersions dependency notations e.g. `Kotlin.stdlib.jdk8`
    useRefreshVersionsNotation = true // defaults to false
  }
  ```
- Migrate to latest [Publish plugin](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
- Add unit tests to generate all examples.
- Log model which causes error
- Add File system storage tests
- Combine geometry classes: Eg. except class app.dokt.common.Point to actual java.awt.Point
- use [.editorconfig](https://editorconfig.org/)

# Links

- [Best practices for authoring maintainable builds](https://docs.gradle.org/current/userguide/authoring_maintainable_build_scripts.html)
- [Common Gradle Plugin Mistakes and Good Practices](https://marcelkliemannel.com/articles/2022/common-gradle-plugin-mistakes-and-good-practices/)
- [Best Practices when using Gradle](https://github.com/liutikas/gradle-best-practices)
- [Gradle Best Practices Plugin](https://github.com/autonomousapps/gradle-best-practices-plugin)
- [10 Gradle best practices to supercharge your project](https://gradlehero.com/gradle-best-practices/)
