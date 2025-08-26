package com.rokt.roktux.utils

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.testutil.assertions.assertGraphicsLayer
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExtensionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `tryCast with castable type runs block`() {
        "test".tryCast<String> {
            return
        }
        Assert.fail()
    }

    @Test
    fun `tryCast with uncastable type does not run block`() {
        10.tryCast<String> {
            Assert.fail()
        }
    }

    @Test
    fun `tryCast with polymorphic type runs block`() {
        val int: Int = 10
        int.tryCast<Number> {
            return
        }
        Assert.fail()
    }

    @Test
    fun `test breakpointIndex`() {
        val breakpoints = persistentMapOf("default" to 0, "tablet" to 512, "desktop" to 640, "tv" to 800)

        val index1 = getBreakpointIndex(200, breakpoints) // index1 = 0
        val index2 = getBreakpointIndex(550, breakpoints) // index2 = 1
        val index3 = getBreakpointIndex(640, breakpoints) // index3 = 2
        val index4 = getBreakpointIndex(801, breakpoints) // index3 = 3
        assertEquals(0, index1)
        assertEquals(1, index2)
        assertEquals(2, index3)
        assertEquals(3, index4)
    }

    @Test
    fun `fadeInOutAnimationModifier sets alpha to 0 and executes callback when animationState changes to Hide`() {
        composeTestRule.setContent {
            FadeInOutButton(AnimationState.Show)
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("done")
            .assertExists()
            .assertGraphicsLayer(alpha = 0F)
    }

    @Test
    fun `fadeInOutAnimationModifier sets alpha to 1 and does not execute callback when animationState changes to Show`() {
        composeTestRule.setContent {
            FadeInOutButton(AnimationState.Hide)
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("test")
            .assertExists()
            .assertGraphicsLayer(alpha = 1F)
    }

    @Test
    fun `layoutExitAnimationModifier executes callback when animationState changes to hide when uiModel is Overlay`() {
        composeTestRule.setContent {
            LayoutExitButton(
                AnimationState.Show,
                LayoutSchemaUiModel.OverlayUiModel(
                    ownModifiers = null,
                    containerProperties = null,
                    allowBackdropToClose = false,
                    conditionalTransitionModifiers = null,
                    child = LayoutSchemaUiModel.ColumnUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        isScrollable = false,
                        children = persistentListOf(),
                    ),
                ),
            )
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("done")
            .assertExists()
    }

    @Test
    fun `layoutExitAnimationModifier executes callback when animationState changes to hide when uiModel is Bottomsheet`() {
        composeTestRule.setContent {
            LayoutExitButton(
                AnimationState.Show,
                LayoutSchemaUiModel.BottomSheetUiModel(
                    ownModifiers = null,
                    containerProperties = null,
                    allowBackdropToClose = false,
                    conditionalTransitionModifiers = null,
                    minimizable = false,
                    child = LayoutSchemaUiModel.ColumnUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        isScrollable = false,
                        children = persistentListOf(),
                    ),
                ),
            )
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("done")
            .assertExists()
    }

    @Test
    fun `layoutExitAnimationModifier executes callback when animationState changes to hide when uiModel is not Overlay or Bottomsheet`() {
        composeTestRule.setContent {
            LayoutExitButton(
                AnimationState.Show,
                LayoutSchemaUiModel.RowUiModel(
                    ownModifiers = null,
                    containerProperties = null,
                    conditionalTransitionModifiers = null,
                    isScrollable = false,
                    children = persistentListOf(
                        LayoutSchemaUiModel.ColumnUiModel(
                            ownModifiers = null,
                            containerProperties = null,
                            conditionalTransitionModifiers = null,
                            isScrollable = false,
                            children = persistentListOf(),
                        ),
                    ),
                ),
            )
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("done")
            .assertExists()
    }

    @Test
    fun `layoutExitAnimationModifier does not execute callback when animationState changes to show when uiModel is Overlay`() {
        composeTestRule.setContent {
            LayoutExitButton(
                AnimationState.Hide,
                LayoutSchemaUiModel.OverlayUiModel(
                    ownModifiers = null,
                    containerProperties = null,
                    allowBackdropToClose = false,
                    conditionalTransitionModifiers = null,
                    child = LayoutSchemaUiModel.ColumnUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        isScrollable = false,
                        children = persistentListOf(),
                    ),
                ),
            )
        }

        composeTestRule.onNodeWithText("test")
            .performClick()

        composeTestRule.onNodeWithText("done")
            .assertDoesNotExist()
    }

    @Composable
    private fun FadeInOutButton(startAnimationState: AnimationState) {
        var animationState by remember {
            mutableStateOf(startAnimationState)
        }
        var text by remember {
            mutableStateOf("test")
        }
        Button(
            onClick = {
                animationState = if (animationState == AnimationState.Hide) {
                    AnimationState.Show
                } else {
                    AnimationState.Hide
                }
            },
            modifier = Modifier.fadeInOutAnimationModifier(animationState, 0) { text = "done" },
        ) {
            Text(text = text)
        }
    }

    @Composable
    private fun LayoutExitButton(startAnimationState: AnimationState, model: LayoutSchemaUiModel) {
        var animationState by remember {
            mutableStateOf(startAnimationState)
        }
        var text by remember {
            mutableStateOf("test")
        }
        Button(
            onClick = {
                animationState = if (animationState == AnimationState.Hide) {
                    AnimationState.Show
                } else {
                    AnimationState.Hide
                }
            },
            modifier = Modifier.layoutExitAnimationModifier(model, animationState, 100) { text = "done" }
                .testTag("button"),
        ) {
            Text(text = text)
        }
    }
}
