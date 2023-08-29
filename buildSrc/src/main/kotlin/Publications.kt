package org.gradle.kotlin.dsl

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

fun Project.createDoktPublication(artifactName: String) = Action<PublishingExtension> {
    //publications.withType<MavenPublication> {
    publications.create<MavenPublication>("maven") {
        //artifact(javadocJar.get()) TODO This is needed?
        pom {
            name.set(artifactName)
            description.set(project.description)
            url.set("https://dokt.app/")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("papinkivi")
                    name.set("Jukka Papinkivi")
                    email.set("jukka@papinkivi.fi")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/dokt/dokt.git")
                developerConnection.set("scm:git:ssh://github.com:dokt/dokt.git")
                url.set("https://github.com/dokt/dokt")
            }
        }
    }
}

fun configureDoktSigning(publishing: PublishingExtension) = Action<SigningExtension> {
    // read from user.home\.gradle\gradle.properties
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}
