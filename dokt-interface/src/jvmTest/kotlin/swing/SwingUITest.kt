package app.dokt.ui.swing

import app.dokt.app.Application
import java.awt.*
import javax.swing.*
import javax.swing.table.TableCellRenderer

object ColorRenderer : JLabel(), TableCellRenderer {
    init { isOpaque = true }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        with(value as Color) {
            background = this
            toolTipText = "RGB: $red, $green, $blue"
        }
        return this
    }
}

@Suppress("EnumEntryName")
enum class Sport {
    Snowboarding,
    Rowing,
    Knitting,
    Speed_reading,
    Pool,
    None_of_the_above;

    override fun toString() = name.replace('_', ' ')
}

data class TestData(
    val firstName: String,
    val lastName: String,
    val favorite: Color,
    val sport: Sport,
    val years: Int,
    val vegetarian: Boolean = false
)

class TestFrame : Frame() {
    init {
        /* Demo data from
           https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
           https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableDialogEditDemoProject/src/components/TableDialogEditDemo.java
         */
        addScrollTable(
            TestData("Kathy", "Smith", Color(153, 0, 153), Sport.Snowboarding, 5),
            TestData("John", "Doe", Color(51, 51, 153), Sport.Rowing, 3, true),
            TestData("Sue", "Black", Color(51, 102, 51), Sport.Knitting, 2),
            TestData("Jane", "White", Color.red, Sport.Speed_reading, 20, true),
            TestData("Joe", "Brown", Color.pink, Sport.Pool, 10)
        ) {
            column("firstName", { firstName }) { preferredWidth = 50 }
            column("lastName",  { lastName }) { preferredWidth = 50 }
            column("favorite",  { favorite }) { preferredWidth = 25 }
            column("sport", { sport }) { preferredWidth = 100 }
            column("years", { years }) { preferredWidth = 50 }
            column("vegetarian", { vegetarian }) { preferredWidth = 50 }
            render<Color>(ColorRenderer)
            selectionListener = ::println
        }
        pack()
    }
}

class TestSwingUI : SwingUI<Application, TestFrame>(){
    override val application = Application()

    override fun createFrame() = TestFrame()
}

fun main() {
    TestSwingUI().start()
}