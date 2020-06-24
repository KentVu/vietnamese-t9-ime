package com.vutrankien.t9vietnamese.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.inputmethod.accessibility.AccessibilityUtils
import com.android.inputmethod.keyboard.KeyboardId
import com.android.inputmethod.keyboard.KeyboardLayoutSet
import com.android.inputmethod.keyboard.MainKeyboardView
import com.android.inputmethod.latin.InputView
import com.android.inputmethod.latin.RichInputMethodManager
import com.android.inputmethod.latin.RichInputMethodSubtype
import com.android.inputmethod.latin.utils.ResourceUtils
import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.vutrankien.t9vietnamese.lib.View as MVPView


class MainActivity : Activity(), MVPView {
    @Inject
    lateinit var logFactory: LogFactory
    private lateinit var log: LogFactory.Log
    @Inject
    lateinit var presenter: Presenter
    private val logic: UiLogic = UiLogic.DefaultUiLogic()

    companion object {
        private const val WAKELOCK_TIMEOUT = 60000L
    }
    private lateinit var subtype: InputMethodSubtype

    override val scope = CoroutineScope(Dispatchers.Main + Job())

    override val eventSource: Channel<EventWithData<Event, Key>> =
        Channel()

    override fun showProgress(bytes: Int) {
        displayInfo(R.string.engine_loading, bytes)
    }

    override fun showKeyboard() {
        log.w("View: TODO: showKeyboard")
        wakelock.run { if(isHeld) release() }
        displayInfo(R.string.notify_initialized)
        //defaultSharedPreferences.edit().putLong("load_time", loadTime).apply()
    }

    override fun showCandidates(candidates: Collection<String>) {
        log.d("View: showCandidates:$candidates")
        logic.updateCandidates(candidates)
    }

    override fun confirmInput(word: String) {
        log.d("View: confirmInput")
        // XXX Is inserting a space here a right place?
        findViewById<EditText>(R.id.editText).append(" $word")
        logic.clearCandidates()
    }

    private lateinit var wakelock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as T9Application).appComponent.inject(this)
        log = logFactory.newLog("MainActivity")
        setContentView(R.layout.main)
        AccessibilityUtils.init(this)
        val inputView = findViewById<InputView>(R.id.dialpad)
        val kbView =
            inputView.findViewById(com.android.inputmethod.latin.R.id.keyboard_view) as MainKeyboardView
        val editorInfo = EditorInfo()
        editorInfo.inputType = InputType.TYPE_CLASS_TEXT
        val builder = KeyboardLayoutSet.Builder(this, editorInfo)
        val res = resources
        val keyboardWidth = ResourceUtils.getDefaultKeyboardWidth(res)
        val keyboardHeight = ResourceUtils.getDefaultKeyboardHeight(res)
        builder.setKeyboardGeometry(keyboardWidth, keyboardHeight)
        RichInputMethodManager.init(this)
        val mRichImm = RichInputMethodManager.getInstance()
        val imi: InputMethodInfo = mRichImm.getInputMethodInfoOfThisIme()
        val subtypeCount = imi.subtypeCount
        for (index in 0 until subtypeCount) {
            //mAllSubtypesList.add(imi.getSubtypeAt(index))
            if (imi.getSubtypeAt(index) != null) {
                subtype = imi.getSubtypeAt(index)!!
                break
            }
        }
        builder.setSubtype(RichInputMethodSubtype.getRichInputMethodSubtype(subtype))
        builder.setIsSpellChecker(true /* isSpellChecker */)
        builder.disableTouchPositionCorrectionData()
        val keyboardLayoutSet = builder.build()
        kbView.setKeyboard(keyboardLayoutSet.getKeyboard(KeyboardId.ELEMENT_ALPHABET))
        kbView.setKeyboardActionListener(
            KeyboardActionListener(
                logFactory,
                scope,
                eventSource
            )
        )
        logic.initializeCandidatesView(findViewById(R.id.candidates_view))
        //val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        //recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        //recyclerView.adapter = wordListAdapter
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakelock = powerManager.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK,
                "${BuildConfig.APPLICATION_ID}:MainActivity")

        wakelock.acquire(WAKELOCK_TIMEOUT)

        presenter.attachView(this)
        scope.launch {
            eventSource.send(Event.START.noData())
        }
    }

    private fun displayInfo(resId: Int, vararg formatArgs: Any) {
        val textView = findViewById<TextView>(R.id.text)
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        textView.text = getString(resId, *formatArgs)
    }

    override fun onStop() {
        super.onStop()
        log.d("onStop")
    }

    override fun onDestroy() {
        log.d("onDestroy")
        super.onDestroy()
    }

    private fun displayError(msg: String) {
        val textView: TextView = findViewById(R.id.text)
        val color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        textView.setTextColor(color)
        textView.text = getString(R.string.oops, msg)
    }

    private fun displayError(e: Exception) {
        displayError(e.message ?: "")
    }

    fun onCandidateClick(view: View) {
        log.d("onCandidateClick()")
        //engine.flush()
        (view as TextView).text = ""
    }

    fun onBtnClick(view: View) {
        val text = (view as Button).text
        val key = text[0]
        log.d("onBtnClick() btn=$key")
        scope.launch {
            eventSource.send(Event.KEY_PRESS.withData(Key.fromNum(key)))
        }
    }

    fun onBtnStarClick(view: View) {
        log.d("onBtnStarClick()")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
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
                    applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imeManager.showInputMethodPicker()
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
