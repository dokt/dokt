package app.dokt.infra

import app.dokt.common.*
import java.awt.Color
import java.awt.image.ColorModel

expect val centerPoint: Point
expect val headless: Boolean
expect val screenSize: Dimension

//expect fun getDisplaySize(index: Int): Dimension

expect class Color(rgb: Int) {
    constructor(r: Int, g: Int, b: Int)
    constructor(r: Float, g: Float, b: Float, a: Float)
    constructor(r: Float, g: Float, b: Float)
    constructor(r: Int, g: Int, b: Int, a: Int)
    constructor(rgba: Int, hasAlpha: Boolean)

    /**
     * Returns the RGB value representing the color in the default sRGB
     * [ColorModel].
     * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
     * blue).
     * @return the RGB value of the color in the default sRGB
     * `ColorModel`.
     * @see java.awt.image.ColorModel.getRGBdefault
     *
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     */
    fun getRGB(): Int

    /**
     * Returns the red component in the range 0-255 in the default sRGB
     * space.
     * @return the red component.
     * @see .getRGB
     */
    fun getRed(): Int

    /**
     * Returns the green component in the range 0-255 in the default sRGB
     * space.
     * @return the green component.
     * @see .getRGB
     */
    fun getGreen(): Int

    /**
     * Returns the blue component in the range 0-255 in the default sRGB
     * space.
     * @return the blue component.
     * @see .getRGB
     */
    fun getBlue(): Int

    /**
     * Returns the alpha component in the range 0-255.
     * @return the alpha component.
     * @see .getRGB
     */
    fun getAlpha(): Int

    fun brighter(): Color
    fun darker(): Color
    fun getRGBComponents(compArray: FloatArray): FloatArray
    fun getRGBColorComponents(compArray: FloatArray): FloatArray
    fun getComponents(compArray: FloatArray): FloatArray
    fun getColorComponents(compArray: FloatArray): FloatArray
    fun getTransparency(): Int
}
const val TRANSPARENCY_OPAQUE = 1
const val TRANSPARENCY_BITMASK = 2
const val TRANSPARENCY_TRANSLUCENT = 3
/** https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md */
val Color.text get() = rgb.toString(16).padStart(6, '0')