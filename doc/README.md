# Dokt documentation

## Features

### Dokt Gradle plugin features

#### Project detection

Dokt detects architecture layer and platform from project using following logic:

If the project:
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

- Migrate to latest [Publish plugin](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
- Add unit tests to generate all examples.
- Log model which causes error
- Add File system storage tests
- Combine geometry classes: Eg. except class app.dokt.common.Point to actual java.awt.Point

# Links

- [Common Gradle Plugin Mistakes and Good Practices](https://marcelkliemannel.com/articles/2022/common-gradle-plugin-mistakes-and-good-practices/)
