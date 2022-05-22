package app.dokt.iface.swing

import javax.swing.*

class Spinner(label: String, value: Int, max: Int, onChange: (newValue: Int) -> Unit) : JPanel() {
    private val editor = JSpinner(SpinnerNumberModel(value, 0, max, 1))
    private val name = Label(label)
    var value: Int
        get() = editor.value as Int
        set(value) { editor.value = value }

    init {
        addChangeListener(onChange)
        editor.setColumns(max.toString().length)
        name.labelFor = editor
        add(name)
        add(editor)
    }

    fun addChangeListener(onChange: (newValue: Int) -> Unit) {
        editor.addChangeListener { onChange(it.spinnerValueInt) }
    }
}
