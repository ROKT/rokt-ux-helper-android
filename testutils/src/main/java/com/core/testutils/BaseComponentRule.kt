package com.core.testutils

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.platform.app.InstrumentationRegistry
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiBreakpoints
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeComponentState
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.DcuiOfferJson
import com.core.testutils.annotations.TestPseudoState
import com.core.testutils.annotations.WindowSize
import org.json.JSONObject
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val DCUI_PLACEHOLDER = "%DCUI%"
private const val DCUI_INNER_LAYOUT_PLACEHOLDER = "%INNER_LAYOUT%"
private const val BREAKPOINTS_PLACEHOLDER = "%BREAKPOINTS%"
private const val DCUI_SLOT_OFFER_PLACEHOLDER = "%OFFER%"

private const val OUTER_LAYOUT_WRAPPER = """
{
  "breakpoints": {
    $BREAKPOINTS_PLACEHOLDER
  },
  "layout": $DCUI_PLACEHOLDER
}
"""

private const val SLOT_WRAPPER = """
{
   "instanceGuid": "",
   "token": "",
   "layoutVariant": {
     "layoutVariantId": "",
     "moduleName": "",
     "layoutVariantSchema": $DCUI_INNER_LAYOUT_PLACEHOLDER
   },
   "offer": $DCUI_SLOT_OFFER_PLACEHOLDER
}
"""

private const val EXPERIENCE_JSON = """
{
  "sessionId": "",
  "token": "token",
  "placementContext": {
    "roktTagId": "",
    "pageInstanceGuid": "",
    "token": "page_token"
  },
  "plugins": [
    {
      "plugin": {
        "id": "",
        "name": "",
        "targetElementSelector": "",
        "config": {
          "instanceGuid": "",
          "token": "",
          "outerLayoutSchema": $DCUI_PLACEHOLDER,
          "layoutVersion": "",
          "slots": [
            $SLOT_WRAPPER
          ]
        }
      }
    }
  ]
}
"""

private const val PARTNER_EXPERIENCE_JSON = """
{
  "sessionId": "",
  "pageContext": {
    "pageInstanceGuid": "",
    "token": "page_token",
    "pageId": "",
    "language" : "en",
    "isPageDetected": true,
    "pageVariantName": ""
  },
    "options": {
    "useDiagnosticEvents": true
  },
  "success": true,
  "plugins": [
    {
      "plugin": {
        "id": "",
        "name": "",
        "targetElementSelector": "",
        "config": {
          "instanceGuid": "",
          "token": "",
          "outerLayoutSchema": $DCUI_PLACEHOLDER,
          "layoutVersion": "",
          "slots": [
            $SLOT_WRAPPER
          ]
        }
      }
    }
  ]
}
"""

private const val DEFAULT_OUTER_LAYOUT_NODE = """
{
  "type": "OneByOneDistribution",
  "node": {
    "transition": {
      "type": "FadeInOut",
      "settings": {
      "duration": 10
      }
     }
  }
}
"""

/**
 * Custom test rule to facilitate DCUI component testing.
 *
 * Example usage
 *
 * <pre>
 * @get:Rule
 * val composeTestRule = createComposeRule()
 * @get:Rule
 * val dcuiComponentRule = DcuiComponentRule(composeTestRule)
 *
 * @Test
 * @DcuiNodeJson(jsonFile = "test_input.json")
 * fun testDcuiComponent() {
 * }
 * </pre>
 *
 */
abstract class BaseComponentRule(
    private val composeTestRule: ComposeContentTestRule,
    private val isUxHelper: Boolean = false,
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        val dcuiNodeJson = description.getAnnotation(DcuiNodeJson::class.java)
        val dcuiNodeComponentState = description.getAnnotation(DcuiNodeComponentState::class.java)
        val dcuiBreakpoints = buildBreakpoints(description.getAnnotation(DcuiBreakpoints::class.java))
        val dcuiOffer = description.getAnnotation(DcuiOfferJson::class.java)
        return if (dcuiNodeJson != null) {
            object : Statement() {
                override fun evaluate() {
                    val testTag = description.getAnnotation(DcuiConfig::class.java)?.componentTag ?: DCUI_COMPONENT_TAG
                    val windowSize = description.getAnnotation(DcuiConfig::class.java)?.windowSize ?: WindowSize()
                    val breakpointIndex = description.getAnnotation(DcuiConfig::class.java)?.breakpointIndex ?: 0
                    val isDarkModeEnabled =
                        description.getAnnotation(DcuiConfig::class.java)?.isDarkModeEnabled ?: false
                    val testInInnerLayout =
                        description.getAnnotation(DcuiConfig::class.java)?.testInInnerLayout ?: false
                    val testPseudoState =
                        description.getAnnotation(DcuiConfig::class.java)?.pseudoState ?: TestPseudoState()
                    val response = parseNodeJsonToString(
                        dcuiNodeJson,
                        dcuiBreakpoints,
                        testInInnerLayout,
                        dcuiOffer,
                    )
                    initializeResponseToUiModel(response, testInInnerLayout, isDarkModeEnabled)
                    if (dcuiNodeJson.loadComponent) {
                        loadComponent(
                            testTag,
                            dcuiNodeComponentState,
                            windowSize,
                            breakpointIndex,
                            testPseudoState,
                            isDarkModeEnabled,
                        )
                    }
                    base.evaluate()
                }
            }
        } else {
            base
        }
    }

    // here initialize the UiModel
    abstract fun initializeResponseToUiModel(response: String, testInInnerLayout: Boolean, isDarkModeEnabled: Boolean)

    abstract fun loadComponent(
        testTag: String,
        dcuiNodeComponentState: DcuiNodeComponentState?,
        windowSize: WindowSize,
        breakpointIndex: Int = 0,
        pseudoState: TestPseudoState,
        isDarkModeEnabled: Boolean,
    )

    private fun parseNodeJsonToString(
        dcuiNodeJson: DcuiNodeJson,
        dcuiBreakpoints: String,
        testInInnerLayout: Boolean,
        dcuiOfferJson: DcuiOfferJson?,
    ): String {
        val outerLayoutJson = OUTER_LAYOUT_WRAPPER
            .replace(
                DCUI_PLACEHOLDER,
                if (testInInnerLayout) {
                    DEFAULT_OUTER_LAYOUT_NODE
                } else {
                    getJsonString(dcuiNodeJson).trimIndent()
                },
            ).trimIndent()
            .replace(BREAKPOINTS_PLACEHOLDER, dcuiBreakpoints).trimIndent()

        val experienceJson = if (isUxHelper) PARTNER_EXPERIENCE_JSON else EXPERIENCE_JSON
        val response = if (testInInnerLayout) {
            var slotResponse =
                SLOT_WRAPPER.replace(DCUI_INNER_LAYOUT_PLACEHOLDER, JSONObject.quote(getJsonString(dcuiNodeJson)))
                    .trimIndent()
            if (dcuiOfferJson != null) {
                slotResponse =
                    slotResponse.replace(DCUI_SLOT_OFFER_PLACEHOLDER, getJsonString(dcuiOfferJson))
                        .trimIndent()
            }
            val response = experienceJson.replace(DCUI_PLACEHOLDER, JSONObject.quote(outerLayoutJson)).trimIndent()
            response.replace(SLOT_WRAPPER, slotResponse).trimIndent()
        } else {
            experienceJson.replace(DCUI_PLACEHOLDER, JSONObject.quote(outerLayoutJson)).trimIndent()
                .replace(SLOT_WRAPPER, "").trimIndent()
        }
        return response
    }

    private fun getJsonString(dcuiNodeJson: DcuiNodeJson): String = if (dcuiNodeJson.jsonString.isNotBlank()) {
        dcuiNodeJson.jsonString
    } else if (dcuiNodeJson.jsonFile.isNotBlank()) {
        InstrumentationRegistry.getInstrumentation().targetContext.assets.open(dcuiNodeJson.jsonFile).use {
            it.reader().readText()
        }
    } else {
        throw IllegalArgumentException("Both jsonString and jsonFile parameters are not valid")
    }

    private fun getJsonString(dcuiNodeJson: DcuiOfferJson): String = if (dcuiNodeJson.jsonString.isNotBlank()) {
        dcuiNodeJson.jsonString
    } else if (dcuiNodeJson.jsonFile.isNotBlank()) {
        InstrumentationRegistry.getInstrumentation().targetContext.assets.open(dcuiNodeJson.jsonFile).use {
            it.reader().readText()
        }
    } else {
        throw IllegalArgumentException("Both jsonString and jsonFile parameters are not valid")
    }

    private fun buildBreakpoints(breakpoints: DcuiBreakpoints?): String = breakpoints?.breakpoints?.joinToString {
        "\"${it.key}\": ${it.value}"
    } ?: ""
}
