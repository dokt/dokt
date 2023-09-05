package app.dokt.generator.code.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementWalkingVisitor
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import kotlin.reflect.KClass

open class ElementException(element: KtElement, message: String, cause: Throwable? = null) :
    Exception("$message In ${element::class.simpleName} '${element.text}'.", cause)

class ElementNotFoundByType(element: KtElement, val type: KClass<out KtElement>) :
    ElementException(element, "Element of type ${type.simpleName} not found!")


val KtElement?.binaries get() = getElements<KtBinaryExpression>()

val KtElement?.booleanAssigns get() = binaries.associate { it.left!!.text!! to it.right!!.text.toBoolean() }

fun KtElement?.findBlock() = findElement<KtBlockExpression>()

fun KtElement?.findCall(to: String) = this?.findElement<KtCallExpression> { calleeName == to }

fun KtElement.findCalls() = getElements<KtCallExpression>()

fun KtElement.findCalls(to: String) = getElements<KtCallExpression> { calleeName == to }

fun KtElement.findDotExpression(instance: String, property: String) = findElement<KtDotQualifiedExpression> {
    text == "$instance.$property"
}

inline fun <reified E : KtElement> KtElement?.findElement() = this?.walkRecursive { it as? E }

inline fun <reified E : KtElement> KtElement.findElement(crossinline predicate: E.() -> Boolean) =
    walkRecursive { if ((it as? E)?.predicate() == true) it else null }

inline fun <reified E : KtElement> KtElement?.getElements(): List<E> {
    if (this == null) return emptyList()
    val list = mutableListOf<E>()
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is E) list.add(element)
            super.visitElement(element)
        }
    })
    return list
}

inline fun <reified E : KtElement> KtElement?.getElements(crossinline predicate: E.() -> Boolean): List<E> {
    if (this == null) return emptyList()
    val list = mutableListOf<E>()
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is E && element.predicate()) list.add(element)
            super.visitElement(element)
        }
    })
    return list
}

fun KtElement.getBlock() = getElement<KtBlockExpression>()

inline fun <reified E : KtElement> KtElement.getElement() = findElement<E>() ?: notFound<E>()

fun KtElement.has(predicate: KtElement.() -> Boolean) = walkRecursive { if (it.predicate()) true else null } ?: false

inline fun <reified E : KtElement> KtElement.hasElement(crossinline predicate: E.() -> Boolean) = walkRecursive {
    if (it is E && it.predicate()) true else null
} ?: false

val KtElement.callStringValue get() = getElement<KtCallExpression>().getElement<KtLiteralStringTemplateEntry>().text!!

fun KtElement?.hasCall(to: String) = this?.hasElement<KtCallExpression> { calleeName == to } == true

fun KtElement.hasCallValue(value: String) = hasElement<KtCallExpression> {
    hasElement<KtLiteralStringTemplateEntry> { text == value }
}

fun KtElement.hasDotExpression(instance: String, property: String) = hasElement<KtDotQualifiedExpression> {
    text == "$instance.$property"
}

inline fun <reified E : KtElement> KtElement.notFound(): E { throw ElementNotFoundByType(this, E::class) }

fun KtElement.visitRecursive(visitor: PsiRecursiveElementVisitor.(KtElement) -> Unit) {
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            // Skip non KtElements (usually whitespace).
            if (element is KtElement) visitor(element)
            super.visitElement(element)
        }
    })
}

fun <R> KtElement.walkRecursive(visitor: PsiRecursiveElementWalkingVisitor.(KtElement) -> R): R? {
    var result: R? = null
    accept(object : PsiRecursiveElementWalkingVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is KtElement) {
                result = visitor(element)
                if (result == null) super.visitElement(element)
                else stopWalking()
            } else super.visitElement(element) // Skip non KtElements (usually whitespace).
        }
    })
    return result
}
