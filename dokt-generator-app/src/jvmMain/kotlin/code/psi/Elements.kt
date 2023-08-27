package app.dokt.generator.code.psi

import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.psi.*
import kotlin.reflect.KClass

open class ElementException(val element: KtElement, message: String, cause: Throwable? = null) :
    Exception("$message In ${element::class.simpleName} '${element.text}'.", cause)

class ElementNotFoundByType(element: KtElement, val type: KClass<out KtElement>) :
    ElementException(element, "Element of type ${type.simpleName} not found!")

fun KtElement.findBinaries() = findElements<KtBinaryExpression>()

fun KtElement.findCall(to: String) = findElement<KtCallExpression> { calleeName == to }

fun KtElement.findCalls() = findElements<KtCallExpression>()

fun KtElement.findCalls(to: String) = findElements<KtCallExpression> { calleeName == to }

inline fun <reified E : KtElement> KtElement.findElement() = walkRecursive { it as? E }

inline fun <reified E : KtElement> KtElement.findElement(crossinline predicate: E.() -> Boolean) =
    walkRecursive { if ((it as? E)?.predicate() == true) it else null }

inline fun <reified E : KtElement> KtElement.findElements(): List<E> {
    val list = mutableListOf<E>()
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is E) list.add(element)
            super.visitElement(element)
        }
    })
    return list
}

inline fun <reified E : KtElement> KtElement.findElements(crossinline predicate: E.() -> Boolean): List<E> {
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

fun KtElement.hasCallValue(value: String) = hasElement<KtCallExpression> {
    hasElement<KtLiteralStringTemplateEntry> { text == value }
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
