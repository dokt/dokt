package app.dokt.ui.swing

import app.dokt.get
import app.dokt.ui.*
import java.awt.*
import java.util.*
import javax.swing.*
import javax.swing.table.*
import kotlin.reflect.KClass

@DslMarker
annotation class TableMarker

/** Table column specification */
@TableMarker
class Column<D, T : Any>(val key: String, val map: D.() -> T?, val type: KClass<T>) {
    var preferredWidth: Int? = null
}

/** Table specification */
@TableMarker
class Table<D>(val name: String = "", vararg rows: D) {
    val columns = mutableListOf<Column<D, *>>()
    var configure: JTable.() -> Unit = {}
    var fillViewportHeight = false

    /** Row selection listener */
    var selectionListener: ((D) -> Unit)? = null

    /** Default renderers */
    val renderers = mutableMapOf<KClass<*>, TableCellRenderer>()

    val rows = rows.toMutableList()
    var rowHeight: Int? = null

    inline fun <reified T> render(renderer: TableCellRenderer) { renderers[T::class] = renderer }
}

private class LocalizedTableModel<D>(private val table: Table<D>) : AbstractTableModel(), Localized {
    private val columns get() = table.columns
    val rows get() = table.rows

    override fun getColumnClass(column: Int) = columns[column].type.java

    override fun getColumnCount() = columns.size

    override fun getColumnName(column: Int) = columns[column].key.let { bundle.get("${table.name}.$it", it) }

    override fun getRowCount() = rows.size

    override fun getValueAt(row: Int, column: Int) = columns[column].map(rows[row])

    override fun localize(locale: Locale) = fireTableStructureChanged()
}

fun <D> Container.addScrollTable(vararg rows: D, init: Table<D>.() -> Unit) =
    add(JScrollPane(table(*rows, init = init))) as JScrollPane

fun <D> Container.addTable(vararg rows: D, init: Table<D>.() -> Unit): JTable {
    val table = table(*rows, init = init)
    add(table.tableHeader, BorderLayout.NORTH)
    add(table)
    return table
}

fun <D> Component.table(vararg rows: D, init: Table<D>.() -> Unit): JTable {
    val table = Table(name, *rows).apply(init)
    val model = LocalizedTableModel(table)
    return JTable(model).apply {
        table.columns.forEachIndexed { index, column -> columnModel.getColumn(index).apply {
            column.preferredWidth?.let { preferredWidth = it }
        } }
        table.renderers.forEach { (columnClass, renderer) -> setDefaultRenderer(columnClass.java, renderer) }
        fillsViewportHeight = table.fillViewportHeight
        table.rowHeight?.let { rowHeight = it }
        table.selectionListener?.let { listener ->
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            selectionModel.addListSelectionListener { if (!it.valueIsAdjusting) listener(model.rows[selectedRow]) }
        }
        table.configure(this)
    }
}

inline fun <D, reified T : Any> Table<D>.column(key: String, noinline map: D.() -> T?) = column(key, map) { }

inline fun <D, reified T : Any> Table<D>.column(key: String, noinline map: D.() -> T?, init: Column<D, T>.() -> Unit) =
    Column(key, map, T::class).apply {
        init()
        this@column.columns.add(this)
    }
