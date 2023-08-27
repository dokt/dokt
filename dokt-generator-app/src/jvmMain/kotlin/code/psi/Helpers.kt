package app.dokt.generator.code.psi

import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.psi.*

val KtCallExpression.calleeName get() = (calleeExpression as? KtNameReferenceExpression)?.getReferencedName()

fun KtElement.findCall(to: String) = findElement<KtCallExpression> { calleeName == to }

fun KtElement.findCalls() = findElements<KtCallExpression>()

fun KtElement.findCalls(to: String) = findElements<KtCallExpression> { calleeName == to }

inline fun <reified E : PsiElement> KtElement.findElement() = walkRecursive { it as? E }

inline fun <reified E : PsiElement> KtElement.findElement(crossinline predicate: E.() -> Boolean) =
    walkRecursive { if ((it as? E)?.predicate() == true) it else null }

inline fun <reified E : PsiElement> KtElement.findElements(): List<E> {
    val list = mutableListOf<E>()
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is E) list.add(element)
            super.visitElement(element)
        }
    })
    return list
}

inline fun <reified E : PsiElement> KtElement.findElements(crossinline predicate: E.() -> Boolean): List<E> {
    val list = mutableListOf<E>()
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is E && element.predicate()) list.add(element)
            super.visitElement(element)
        }
    })
    return list
}

fun KtElement.visitRecursive(visitor: PsiRecursiveElementVisitor.(PsiElement) -> Unit) {
    accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            visitor(element)
            super.visitElement(element)
        }
    })
}

fun <R> KtElement.walkRecursive(visitor: PsiRecursiveElementWalkingVisitor.(PsiElement) -> R): R? {
    var result: R? = null
    accept(object : PsiRecursiveElementWalkingVisitor() {
        override fun visitElement(element: PsiElement) {
            result = visitor(element)
            if (result == null) super.visitElement(element)
            else stopWalking()
        }
    })
    return result
}
