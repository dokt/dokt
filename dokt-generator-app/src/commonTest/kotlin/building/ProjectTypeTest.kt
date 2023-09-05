package app.dokt.generator.building

import app.dokt.generator.building.ProjectType.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProjectTypeTest : FunSpec({
    context("parse") {
        @Suppress("SpellCheckingInspection")
        mapOf(
            "" to ROOT,
            "build" to null,
            "doc" to null,
            "documents" to null,
            "gradle" to null,
            "src" to null,
            "x-skip" to null,
            "foo-test" to INFRASTRUCTURE,
            "foo-test-jvm" to INFRASTRUCTURE_JVM,
            "jvm:foo-test" to INFRASTRUCTURE_JVM,
            "foo-test-js" to INFRASTRUCTURE_JS,
            "js:foo-test" to INFRASTRUCTURE_JS,
            "dom" to DOMAINS,
            "domain" to DOMAINS,
            "domains" to DOMAINS,
            "foo-dom" to DOMAIN,
            "foo-domain" to DOMAIN,
            "foo-app" to APPLICATION,
            "foo-application" to APPLICATION,
            "foo-if" to INTERFACE,
            "foo-iface" to INTERFACE,
            "foo-if-jvm" to INTERFACE_JVM,
            "jvm:foo-if" to INTERFACE_JVM,
            "foo-if-js" to INTERFACE_JS,
            "js:foo-if" to INTERFACE_JS,
            "foo-api" to KTOR_SERVER,
            "foo-ktor-server" to KTOR_SERVER,
            "foo-ktor-svr" to KTOR_SERVER,
            "foo-ktor-srv" to KTOR_SERVER,
            "foo-swing" to SWING,
            "foo-swt" to SWT,
            "foo" to INFRASTRUCTURE,
            "foo-infra" to INFRASTRUCTURE,
            "foo-infrastructure" to INFRASTRUCTURE,
            "foo-jvm" to INFRASTRUCTURE_JVM,
            "foo-infra-jvm" to INFRASTRUCTURE_JVM,
            "foo-infrastructure-jvm" to INFRASTRUCTURE_JVM,
            "jvm" to INFRASTRUCTURE_JVM,
            "jvm-projects" to INFRASTRUCTURE_JVM,
            "jvm:foo" to INFRASTRUCTURE_JVM,
            "jvm:foo-infra" to INFRASTRUCTURE_JVM,
            "jvm:foo-infrastructure" to INFRASTRUCTURE_JVM,
            "foo-js" to INFRASTRUCTURE_JS,
            "foo-infra-js" to INFRASTRUCTURE_JS,
            "foo-infrastructure-js" to INFRASTRUCTURE_JS,
            "js" to INFRASTRUCTURE_JS,
            "js-projects" to INFRASTRUCTURE_JS,
            "js:foo" to INFRASTRUCTURE_JS,
            "js:foo-infra" to INFRASTRUCTURE_JS,
            "js:foo-infrastructure" to INFRASTRUCTURE_JS
        ).map { (name, type) -> test(name.ifEmpty { ":" }) { ProjectType.parse(name) shouldBe type } }
    }
})
