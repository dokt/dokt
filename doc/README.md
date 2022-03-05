# Dokt documentation

## Features

### Dokt Gradle plugin features

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
