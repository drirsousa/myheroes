package br.com.rosait.myheroes.view

import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import br.com.rosait.myheroes.R
import br.com.rosait.myheroes.view.home.main.MainActivity
import org.junit.Test

class MainActivityTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun shouldDisplayTitle() {
        launchActivity<MainActivity>().apply {
            val expectedTitle = context.getString(R.string.lbl_item_list_toolbar)

            moveToState(Lifecycle.State.RESUMED)

            Espresso.onView(ViewMatchers.withText(expectedTitle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }
}