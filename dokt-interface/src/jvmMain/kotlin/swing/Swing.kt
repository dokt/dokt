@file:Suppress("unused", "UNCHECKED_CAST")

package app.dokt.ui.swing

import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JSlider
import javax.swing.JSpinner
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent

fun button(text: String, action: ActionListener) = JButton(text).apply { addActionListener(action) }

val ChangeEvent.sliderValue get() = (source as JSlider).value

val ChangeEvent.spinnerValueInt get() = (source as JSpinner).value as Int


fun JComponent.add(components: List<JComponent>) { components.forEach { add(it) } }

fun JComponent.addButton(text: String, action: ActionListener) = add(button(text, action)) as JButton

fun JComponent.addFiller() {
    add(Filler())
}

fun JComponent.setBorderTitle(title: String) {
    border = BorderFactory.createTitledBorder(title)
}

fun JComponent.setPreferredSize(width: Int, height: Int) {
    preferredSize = Dimension(width, height)
}

val JSpinner.defaultEditor get() = editor as JSpinner.DefaultEditor

fun JSpinner.setColumns(value: Int) {
    defaultEditor.textField.columns = value
}

fun <E> ListSelectionEvent.getSelectedValue() = (source as JList<E>).selectedValue!!
