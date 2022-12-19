import com.github.kentvu.t9vietnamese.DefaultApp
import com.github.kentvu.t9vietnamese.model.*
import okio.FileSystem

//import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    //runBlocking {    }
    val app = DefaultApp(
        Keyboard(listOf(
            KeysCollection.key1,
            KeysCollection.key2,
            KeysCollection.key3,
            KeysCollection.key4,
            KeysCollection.key5,
            KeysCollection.key6,
            KeysCollection.key7,
            KeysCollection.key8,
            KeysCollection.key9,
            KeysCollection.key0,
        )),
        VietnameseWordList,
        FileSystem.SYSTEM
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