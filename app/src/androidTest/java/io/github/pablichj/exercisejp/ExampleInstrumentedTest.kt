package io.github.pablichj.exercisejp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.pablichj.exercisejp.ui.SearchPageContent
import io.github.pablichj.exercisejp.ui.SearchPageState
import io.github.pablichj.exercisejp.ui.theme.ExerciseJPTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {    // setting our composable as content for test
            ExerciseJPTheme {
                var pageState by remember { mutableStateOf(SearchPageState()) }
                SearchPageContent(pageState = pageState, onSubmitClick = {})
            }
        }
    }

    @Test
    fun verify_that_all_views_exists() {
        composeTestRule.onNodeWithTag("SearchPageContentRootNode").assertExists()
        composeTestRule.onNodeWithText("Enter City Name").assertExists()
        composeTestRule.onNodeWithText("Submit").assertExists()
    }
}
