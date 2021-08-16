package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel


class MainActivity : Activity() {
    private val logFactory: LogFactory = AndroidLogFactory
    private val log = logFactory.newLog("MainActivity")
    lateinit var presenter: Presenter

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val channel: Channel<EventWithData<Event, Key>> = Channel()

    private val preferences by lazy { Preferences(applicationContext) }

    private lateinit var view: MainActivityView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        view = MainActivityView(
            logFactory,
            this,
            scope,
            channel,
            findViewById(R.id.text),
            findViewById<EditText>(R.id.editText).onCreateInputConnection(EditorInfo())
        )
        presenter = Presenter(
            logFactory,
            DefaultT9Engine(
                DecomposedSeed(resources),
                VnPad,
                logFactory,
                TrieDb(logFactory, AndroidEnv(applicationContext))
            ),
            view
        )
        view.init(findViewById(R.id.dialpad), findViewById(R.id.candidates_view))

        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        log.d("onStop")
    }

    override fun onDestroy() {
        log.d("onDestroy")
        scope.cancel("MainActivity#onDestroy")
        super.onDestroy()
    }

    fun onCandidateClick(view: View) {
        log.d("onCandidateClick()")
        // TODO select
        (view as TextView).text = ""
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        val key = text[0]
        //log.d("onBtnClick() text=$text")
        this.view.onBtnClick(key)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        log.d("onKeyDown:$keyCode,$event")
        view.onKeyDown(event)
        return true
    }

    fun onBtnStarClick(view: View) {
        log.d("onBtnStarClick()")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val autoScroll = menu!!.findItem(R.id.auto_scroll_candidate)
        autoScroll.isChecked = preferences.autoScroll
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.enable_ime -> {
                startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
                return true
            }
            R.id.select_system_ime -> {
                val imeManager: InputMethodManager =
                    applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imeManager.showInputMethodPicker()
                return true
            }
            R.id.auto_scroll_candidate -> {
                item.isChecked = !item.isChecked
                preferences.autoScroll = item.isChecked
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log.d("onActivityResult:$requestCode:res=$resultCode:data=$data")
        super.onActivityResult(requestCode, resultCode, data)
    }
}
