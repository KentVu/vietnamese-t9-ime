package com.vutrankien.t9vietnamese.android.tests

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

object TestHelpers {
    // https://github.com/travis-ci/travis-ci/issues/6340#issuecomment-239537244
    internal fun Activity.unlockScreen() {
        val wakeUpDevice = Runnable {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        this.runOnUiThread(wakeUpDevice)
    }

    private const val POLL_INTERVAL = 50L

    fun waitUiAction(
        condition: Matcher<View>,
        timeout: Long
    ): ViewAction? {
        return object : ViewAction {
            /*isDisplayed()*/
            override fun getConstraints(): Matcher<View> =
                CoreMatchers.any(View::class.java) /*isDisplayed()*/

            override fun getDescription(): String {
                return "Wait for view with condition: $condition."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeout
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (condition.matches(child)) {
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(POLL_INTERVAL)
                } while (System.currentTimeMillis() < endTime)
                throw PerformException.Builder()
                    .withActionDescription(getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }
        }
    }

    fun waitUntilViewFound(
        condition: Matcher<View>,
        timeoutMs: Long
    ): ViewInteraction {
        return onView(isRoot()).perform(
            waitUiAction(
                condition,
                timeoutMs
            )
        )
    }
}