package app.dokt.ui.swing

import app.dokt.common.upperFirst
import javax.swing.JLabel

class Label(label: String) : JLabel("${label.upperFirst}:")
