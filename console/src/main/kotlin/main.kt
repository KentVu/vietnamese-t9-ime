import com.github.kentvu.t9vietnamese.App
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.WordList
//import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    //runBlocking {    }
    println("Hello World!")
    val app = App(
        Keyboard(Key('1', "a")),
        WordList.Default(setOf("aa", "bb2", "cc1", "dd2"))
    )
    app.init()
    app.type('1')
    println(app.candidates)
}