package app.dokt.generator.code.psi

import app.dokt.generator.code.psi.Psi

val String.kts get() = Psi.parseKts(this)
