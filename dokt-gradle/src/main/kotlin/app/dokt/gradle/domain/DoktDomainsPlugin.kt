package app.dokt.gradle.domain

import app.dokt.gradle.common.ProjectPlugin

class DoktDomainsPlugin : ProjectPlugin(DoktDomainPlugin::class) {
    override val pluginLabel get() = "Dokt parent of domain layers"
}
