package app.dokt.generator.code.psi

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

val KtCallExpression.calleeName get() = (calleeExpression as? KtNameReferenceExpression)?.getReferencedName()

val KtCallExpression.stringValues get() = findElements<KtLiteralStringTemplateEntry>().map { it.text!! }
