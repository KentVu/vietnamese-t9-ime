import com.github.kentvu.t9vietnamese.lib.VNT9App
import com.github.kentvu.t9vietnamese.model.*
import okio.FileSystem

//import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    //runBlocking {    }
    val app = VNT9App(
        VietnameseWordList,
        FileSystem.SYSTEM
    )
    println(app.describe())
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