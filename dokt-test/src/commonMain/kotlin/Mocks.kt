package app.dokt.test

import io.mockk.mockk

/** Creates a relaxed mock object which allows any method call. */
inline fun <reified T : Any> relaxed(test: T.() -> Unit) = mockk<T>(relaxed = true).test()

/** Creates a nervy mock object which throws exception if any method (not defined) is called. */
inline fun <reified T : Any> nervy(test: T.() -> Unit) = mockk<T>().test()
