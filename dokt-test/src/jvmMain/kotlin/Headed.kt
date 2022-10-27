package app.dokt.test

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import java.awt.GraphicsEnvironment
import kotlin.reflect.KClass

class Headed : EnabledCondition {
    override fun enabled(kclass: KClass<out Spec>) = !GraphicsEnvironment.isHeadless()
}
