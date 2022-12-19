import com.github.kentvu.t9vietnamese.App
import com.github.kentvu.t9vietnamese.model.*

//import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    //runBlocking {    }
    val app = App(
        Keyboard(listOf(KeysCollection.key1, KeysCollection.key2)),
        VietnameseWordList()
    )
    println("App init...")
    app.init()
    println("Init done!")
    do {
        val line: String = readln()
        line.forEach {
            app.type(it)
            print("$it: ")
            println(app.candidates)
        }

    } while (line != "")
}