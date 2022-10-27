package app.dokt.infra

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ObservableTest: FunSpec({
    context("bar") {
        test("changed") {
            var notified = false
            resetBar { notified = true }
            bar = "baz"
            notified shouldBe true
        }

        test("exception") {
            resetBar { throw Exception() }
            bar = "baz"
            bar shouldBe "baz"
        }

        test("get") {
            resetBar { }
            bar shouldBe "bar"
        }

        test("unchanged") {
            var notified = false
            resetBar { notified = true }
            bar = "bar"
            notified shouldBe false
        }

        test("unsubscribe") {
            var a = false
            var b = false
            var c = false
            resetBar { a = true }
            val observer = { _: String -> b = true }
            observers.add(observer)
            observers.add { c = true }
            observers.remove(observer)
            observers.remove { c = true }
            bar = "baz"
            a shouldBe true
            b shouldBe false
            c shouldBe true
        }
    }
    context("Foo") {
        test("changed") {
            var notified = false
            val foo = Foo { notified = true }
            foo.bar = "baz"
            notified shouldBe true
        }

        test("get") {
            val foo = Foo { }
            foo.bar shouldBe "bar"
        }

        test("unchanged") {
            var notified = false
            val foo = Foo { notified = true }
            foo.bar = "bar"
            notified shouldBe false
        }
    }
}) {
    companion object {
        fun resetBar(observer: (String) -> Unit) {
            observers.clear()
            bar = "bar"
            observers.add(observer)
        }
    }
}
