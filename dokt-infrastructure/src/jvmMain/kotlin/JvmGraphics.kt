package app.dokt.infra

import app.dokt.common.*
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

actual val centerPoint: Point get() = environment.centerPoint

private val environment = GraphicsEnvironment.getLocalGraphicsEnvironment()

/**
 * https://stackoverflow.com/questions/41067235/what-is-the-benefit-of-setting-java-awt-headless-true
 * https://www.oracle.com/technical-resources/articles/javase/headless.html
 */
actual val headless = GraphicsEnvironment.isHeadless()

actual val screenSize: Dimension get() = toolkit.screenSize

private val toolkit = Toolkit.getDefaultToolkit()

actual typealias Color = java.awt.Color

//expect fun getDisplaySize(index: Int) = environment.screenDevices[index].fullScreenWindow.
