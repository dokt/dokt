package app.dokt.generator.building.gradle

import app.dokt.generator.code.psi.Psi

val String.kts get() = Psi.parseKts(this)
