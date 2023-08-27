package app.dokt.generator.code.psi

import org.jetbrains.kotlin.psi.*

val KtCallExpression.calleeName get() = (calleeExpression as? KtNameReferenceExpression)?.getReferencedName()
