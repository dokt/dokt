package app.dokt.iface.swing

import jiconfont.IconCode
import jiconfont.icons.font_awesome.FontAwesome
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons
import jiconfont.swing.IconFontSwing

object IconBuilder {
    init {
        IconFontSwing.register(FontAwesome.getIconFont())
        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont())
    }

    fun icon(code: IconCode) = IconFontSwing.buildIcon(code, currentLookAndFeel.iconSize)!!

    fun image(code: IconCode, size: Float = currentLookAndFeel.iconSize) = IconFontSwing.buildImage(code, size)!!
}

val IconCode.icon get() = IconBuilder.icon(this)
val IconCode.image get() = IconBuilder.image(this)
