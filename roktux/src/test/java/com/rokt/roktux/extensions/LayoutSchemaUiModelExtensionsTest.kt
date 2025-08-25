package com.rokt.roktux.extensions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.data.BindData.*
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.StateBlock
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutSchemaUiModelExtensionsTest {

    @Test
    fun `hasModifiersMatching returns false when no modifiers exist`() {
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = null,
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val result = model.hasModifiersMatching { true }
        assertFalse(result)
    }

    @Test
    fun `hasModifiersMatching returns false when no modifiers match predicate`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.Fixed(100f)),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val result = model.hasModifiersMatching { it.default.height is HeightUiModel.MatchParent }
        assertFalse(result)
    }

    @Test
    fun `hasModifiersMatching returns true when modifier matches predicate`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.MatchParent),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val result = model.hasModifiersMatching { it.default.height is HeightUiModel.MatchParent }
        assertTrue(result)
    }

    @Test
    fun `transformModifiers returns same object when no modifiers match predicate`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.Fixed(100f)),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val result = model.transformModifiers(
            predicate = { it.default.height is HeightUiModel.MatchParent },
            transformation = { it.copy(default = it.default.copy(height = HeightUiModel.WrapContent)) },
        )

        assertEquals(model, result)
    }

    @Test
    fun `transformModifiers transforms matching modifiers`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.MatchParent),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val result = model.transformModifiers(
            predicate = { it.default.height is HeightUiModel.MatchParent },
            transformation = { it.copy(default = it.default.copy(height = HeightUiModel.WrapContent)) },
        )

        assert(result !== model)
        assert(result is LayoutSchemaUiModel.BasicTextUiModel)

        val resultModel = result as LayoutSchemaUiModel.BasicTextUiModel
        val resultModifier = resultModel.ownModifiers?.first()
        assert(resultModifier?.default?.height is HeightUiModel.WrapContent)
    }

    @Test
    fun `transformHeightForAllMatching returns same object when no height modifiers match`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.Fixed(100f)),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val newHeight = HeightUiModel.MatchParent
        val result = model.transformHeightForAllMatching(
            condition = { it.default.height is HeightUiModel.WrapContent },
            newHeight = newHeight,
        )

        assertEquals(model, result)
    }

    @Test
    fun `transformHeightForAllMatching transforms matching height modifiers`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.MatchParent),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val newHeight = HeightUiModel.WrapContent
        val result = model.transformHeightForAllMatching(
            condition = { it.default.height is HeightUiModel.MatchParent },
            newHeight = newHeight,
        )

        assert(result !== model)
        assert(result is LayoutSchemaUiModel.BasicTextUiModel)

        val resultModel = result as LayoutSchemaUiModel.BasicTextUiModel
        val resultModifier = resultModel.ownModifiers?.first()
        assertEquals(newHeight, resultModifier?.default?.height)
    }

    @Test
    fun `transformHeightForAllMatching preserves non-matching modifiers`() {
        val modifier1 = StateBlock(
            default = ModifierProperties(height = HeightUiModel.MatchParent),
        )
        val modifier2 = StateBlock(
            default = ModifierProperties(height = HeightUiModel.Fixed(200f)),
        )
        val model = LayoutSchemaUiModel.BasicTextUiModel(
            ownModifiers = persistentListOf(modifier1, modifier2),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            conditionalTransitionTextStyling = null,
            textStyles = null,
            value = Value("test"),
        )

        val newHeight = HeightUiModel.WrapContent
        val result = model.transformHeightForAllMatching(
            condition = { it.default.height is HeightUiModel.MatchParent },
            newHeight = newHeight,
        )

        assert(result !== model)
        val resultModel = result as LayoutSchemaUiModel.BasicTextUiModel
        val resultModifiers = resultModel.ownModifiers

        assertEquals(newHeight, resultModifiers?.get(0)?.default?.height)

        assertEquals(HeightUiModel.Fixed(200f), resultModifiers?.get(1)?.default?.height)
    }

    @Test
    fun `transformHeightForAllMatching works with different LayoutSchemaUiModel types`() {
        val modifier = StateBlock(
            default = ModifierProperties(height = HeightUiModel.MatchParent),
        )
        val model = LayoutSchemaUiModel.RowUiModel(
            ownModifiers = persistentListOf(modifier),
            containerProperties = null,
            conditionalTransitionModifiers = null,
            isScrollable = false,
            children = persistentListOf(),
        )

        val newHeight = HeightUiModel.WrapContent
        val result = model.transformHeightForAllMatching(
            condition = { it.default.height is HeightUiModel.MatchParent },
            newHeight = newHeight,
        )

        assert(result !== model)
        assert(result is LayoutSchemaUiModel.RowUiModel)

        val resultModel = result as LayoutSchemaUiModel.RowUiModel
        val resultModifier = resultModel.ownModifiers?.first()
        assertEquals(newHeight, resultModifier?.default?.height)
    }
}
