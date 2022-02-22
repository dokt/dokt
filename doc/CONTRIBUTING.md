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
