import com.github.kentvu.t9vietnamese.lib.VNT9App
import com.github.kentvu.t9vietnamese.model.*
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import okio.FileSystem

fun main(args: Array<String>) {
    runBlocking {
        Napier.base(DebugAntilog("T9AppConsole"))
        val app = VNT9App(
            VietnameseWordList,
            FileSystem.SYSTEM
        )
        Napier.d(app.describe())
        Napier.i("App init...")
        app.init()
        Napier.i("Init done!")
        //lateinit var job: Job
        val job = launch(Dispatchers.Default) {
            app.eventFlow.collect {
                when (it) {
                    is T9AppEvent.UpdateCandidates ->
                        Napier.d("UpdateCandidates: ${it.candidates}")
                    else -> Napier.d("app event: $it")
                }
            }
        }
        do {
            print("Type $")
            val line: String = readln()
            line.forEach {
                app.type(it)
                Napier.d("typed: $it")
            }
        } while (line != "")
        job.cancel()
    }
}