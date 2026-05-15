package com.rokt.roktux.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.uimodel.CatalogItemGroupOptionModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlin.math.roundToInt

internal class CatalogDropdownComponent(private val modifierFactory: ModifierFactory) :
    ComposableComponent<LayoutSchemaUiModel.CatalogDropdownUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val attribute = model.catalogItemGroup?.attributes?.getOrNull(model.attributeIndex) ?: return
        val options = attribute.options
        if (options.size < MIN_VISIBLE_OPTIONS) return

        val selectedIndex = offerState.customState[model.customStateKey]
            ?.takeIf { it in options.indices }
        var isExpanded by remember { mutableStateOf(false) }
        var headSize by remember { mutableStateOf(IntSize.Zero) }
        var boxBounds by remember { mutableStateOf<IntRect?>(null) }
        var headBounds by remember { mutableStateOf<IntRect?>(null) }
        var popupPlacement by remember { mutableStateOf(CatalogDropdownPopupPlacement.Below) }
        var popupWindowOffset by remember { mutableStateOf(IntOffset.Zero) }
        val headBoundsInBox = remember(headBounds, boxBounds) {
            headBounds?.let { bounds ->
                boxBounds?.let(bounds::relativeTo)
            }
        }
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val view = LocalView.current
        val rootContainer = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )

        Column(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model.ownModifiers,
                    conditionalTransitionModifier = model.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                )
                .then(modifier),
            horizontalAlignment = BiasAlignment.Horizontal(rootContainer.alignmentBias),
            verticalArrangement = rootContainer.verticalArrangement,
        ) {
            Box(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    boxBounds = coordinates.boundsInWindowIntRect()
                },
            ) {
                DropdownHead(
                    model = model,
                    displayText = displayText(model, selectedIndex, offerState),
                    isExpanded = isExpanded,
                    isSelected = selectedIndex != null,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    offerState = offerState,
                    onEventSent = onEventSent,
                    onPositioned = { coordinates, padding ->
                        val bounds = coordinates.boundsInWindowIntRect()
                            .inflate(padding = padding, density = density, layoutDirection = layoutDirection)
                        popupWindowOffset = view.windowToScreenOffset()
                        headSize = IntSize(width = bounds.width, height = bounds.height)
                        headBounds = bounds
                    },
                    modifier = Modifier,
                    onClick = { isExpanded = !isExpanded },
                )

                if (isExpanded) {
                    Popup(
                        popupPositionProvider = remember(
                            headSize.height,
                            headBoundsInBox,
                            popupWindowOffset,
                        ) {
                            CatalogDropdownPopupPositionProvider(
                                anchorHeight = headSize.height,
                                measuredAnchorBoundsInParent = headBoundsInBox,
                                windowOffset = popupWindowOffset,
                                onPlacementCalculated = { placement ->
                                    popupPlacement = placement
                                },
                            )
                        },
                        onDismissRequest = { isExpanded = false },
                        properties = PopupProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            focusable = true,
                        ),
                    ) {
                        val headWidth = with(density) { headSize.width.toDp() }
                        Box(
                            modifier = Modifier
                                .requiredWidth(headWidth),
                        ) {
                            OptionList(
                                model = model,
                                selectedIndex = selectedIndex,
                                isDarkModeEnabled = isDarkModeEnabled,
                                breakpointIndex = breakpointIndex,
                                offerState = offerState,
                                onEventSent = onEventSent,
                                borderCornerRadiusMode = popupPlacement.borderCornerRadiusMode,
                                modifier = Modifier.fillMaxWidth(),
                                onSelected = { optionIndex ->
                                    val updatedSelections =
                                        selectedIndices(model, offerState) + (model.attributeIndex to optionIndex)
                                    onEventSent(
                                        LayoutContract.LayoutEvent.SetCustomState(
                                            model.customStateKey,
                                            optionIndex,
                                        ),
                                    )
                                    resolveActiveCatalogItemIndex(model, updatedSelections)?.let { activeItemIndex ->
                                        onEventSent(LayoutContract.LayoutEvent.SetActiveCatalogItem(activeItemIndex))
                                    }
                                    isExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DropdownHead(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        displayText: String,
        isExpanded: Boolean,
        isSelected: Boolean,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
        onPositioned: (LayoutCoordinates, PaddingValues?) -> Unit,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val resolvedStyle = model.head.resolve(isSelected = isSelected, isDisabled = false, isErrored = false)
        val style = resolvedStyle.style
        val resolvedIconStyle = model.icon.resolve(isSelected = isSelected, isDisabled = false, isErrored = false)
        val iconStyle = resolvedIconStyle.style
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = style?.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
            baseProperties = resolvedStyle.baseStyle?.containerProperties,
        )
        val textStyle = modifierFactory.createTextStyle(
            text = displayText,
            textStyles = style?.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            baseStyles = resolvedStyle.baseStyle?.textStyles,
            offerState = offerState,
            onEventSent = onEventSent,
        )
        val iconTextStyle = modifierFactory.createTextStyle(
            text = if (isExpanded) ExpandedIcon else CollapsedIcon,
            textStyles = iconStyle?.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            defaultFontFamily = ROKT_ICONS_FONT_FAMILY,
            baseStyles = resolvedIconStyle.baseStyle?.textStyles,
            offerState = offerState,
            onEventSent = onEventSent,
        )
        val padding = resolvedStyle.resolvedPadding(
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
        )

        Row(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = style?.ownModifiers,
                    conditionalTransitionModifier = null,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                    basePropertiesList = resolvedStyle.baseStyle?.ownModifiers,
                )
                .then(modifier)
                .onGloballyPositioned { coordinates -> onPositioned(coordinates, padding) }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    contentDescription = model.a11yLabel ?: displayText
                },
            horizontalArrangement = container.horizontalArrangement,
            verticalAlignment = BiasAlignment.Vertical(container.alignmentBias),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = textStyle.value,
                style = textStyle.textStyle,
            )
            Text(
                text = iconTextStyle.value,
                style = iconTextStyle.textStyle,
            )
        }
    }

    @Composable
    private fun OptionList(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        selectedIndex: Int?,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
        borderCornerRadiusMode: BorderCornerRadiusMode,
        modifier: Modifier = Modifier,
        onSelected: (Int) -> Unit,
    ) {
        val resolvedStyle = model.optionList.resolve(isSelected = false, isDisabled = false, isErrored = false)
        val style = resolvedStyle.style
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = style?.containerProperties,
            index = breakpointIndex,
            isPressed = false,
            baseProperties = resolvedStyle.baseStyle?.containerProperties,
        )

        Column(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = style?.ownModifiers,
                    conditionalTransitionModifier = null,
                    breakpointIndex = breakpointIndex,
                    isPressed = false,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                    basePropertiesList = resolvedStyle.baseStyle?.ownModifiers,
                    borderCornerRadiusMode = borderCornerRadiusMode,
                )
                .then(modifier),
            horizontalAlignment = BiasAlignment.Horizontal(container.alignmentBias),
            verticalArrangement = container.verticalArrangement,
        ) {
            model.catalogItemGroup?.attributes?.getOrNull(model.attributeIndex)?.options.orEmpty()
                .forEachIndexed { index, option ->
                    val disabled = isOptionDisabled(model, index, offerState)
                    OptionRow(
                        model = model,
                        option = option,
                        isSelected = selectedIndex == index,
                        isDisabled = disabled,
                        isDarkModeEnabled = isDarkModeEnabled,
                        breakpointIndex = breakpointIndex,
                        offerState = offerState,
                        onEventSent = onEventSent,
                        onClick = { onSelected(index) },
                    )
                }
        }
    }

    @Composable
    private fun OptionRow(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        option: CatalogItemGroupOptionModel,
        isSelected: Boolean,
        isDisabled: Boolean,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
        onClick: () -> Unit,
    ) {
        val resolvedStyle = model.option.resolve(
            isSelected = isSelected,
            isDisabled = isDisabled,
            isErrored = false,
        )
        val style = resolvedStyle.style
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val textStyle = modifierFactory.createTextStyle(
            text = option.label.orEmpty(),
            textStyles = style?.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            baseStyles = resolvedStyle.baseStyle?.textStyles,
            offerState = offerState,
            onEventSent = onEventSent,
        )
        val selectedIconTextStyle = modifierFactory.createTextStyle(
            text = SelectedOptionMarker,
            textStyles = style?.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            defaultFontFamily = ROKT_ICONS_FONT_FAMILY,
            baseStyles = resolvedStyle.baseStyle?.textStyles,
            offerState = offerState,
            onEventSent = onEventSent,
        )

        Row(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = style?.ownModifiers,
                    conditionalTransitionModifier = null,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                    basePropertiesList = resolvedStyle.baseStyle?.ownModifiers,
                )
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {}
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isDisabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = textStyle.value,
                style = textStyle.textStyle,
            )
            if (isSelected) {
                Spacer(modifier = Modifier.weight(0.05f))
                Text(text = selectedIconTextStyle.value, style = selectedIconTextStyle.textStyle)
            }
        }
    }

    private fun displayText(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        selectedIndex: Int?,
        offerState: OfferUiState,
    ): String {
        val attribute = model.catalogItemGroup?.attributes?.getOrNull(model.attributeIndex)
        if (selectedIndex != null) {
            val option = attribute?.options?.getOrNull(selectedIndex)
            return if (isOptionDisabled(model, selectedIndex, offerState)) {
                model.unavailableValue ?: option?.label.orEmpty()
            } else {
                option?.label.orEmpty()
            }
        }
        return model.placeholderValue ?: attribute?.label.orEmpty()
    }

    private fun LayoutSchemaUiModel.CatalogDropdownElementUiModel?.resolve(
        isSelected: Boolean,
        isDisabled: Boolean,
        isErrored: Boolean,
    ): ResolvedCatalogDropdownStyle {
        val default = this?.default
        val style = when {
            isErrored && this?.errored != null -> errored
            isDisabled && this?.disabled != null -> disabled
            isSelected && this?.selected != null -> selected
            else -> default
        }
        return ResolvedCatalogDropdownStyle(
            style = style,
            baseStyle = if (style === default) null else default,
        )
    }

    private fun selectedIndices(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        offerState: OfferUiState,
    ): Map<Int, Int> {
        val attributes = model.catalogItemGroup?.attributes ?: return emptyMap()
        return attributes.indices.mapNotNull { attributeIndex ->
            offerState.customState[selectionStateKey(attributeIndex)]?.let { selectedIndex ->
                if (selectedIndex in attributes[attributeIndex].options.indices) {
                    attributeIndex to selectedIndex
                } else {
                    null
                }
            }
        }.toMap()
    }

    private fun isOptionDisabled(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        optionIndex: Int,
        offerState: OfferUiState,
    ): Boolean {
        val group = model.catalogItemGroup ?: return false
        val attribute = group.attributes.getOrNull(model.attributeIndex) ?: return false
        val option = attribute.options.getOrNull(optionIndex) ?: return false
        if (option.catalogItemIds.isEmpty()) return false

        var eligibleCatalogItemIds = group.catalogItemIds.toSet()
        selectedIndices(model, offerState)
            .filterKeys { selectedAttributeIndex -> selectedAttributeIndex != model.attributeIndex }
            .forEach { (selectedAttributeIndex, selectedOptionIndex) ->
                val selectedOption = group.attributes
                    .getOrNull(selectedAttributeIndex)
                    ?.options
                    ?.getOrNull(selectedOptionIndex)
                selectedOption?.catalogItemIds?.let { selectedCatalogItemIds ->
                    eligibleCatalogItemIds = eligibleCatalogItemIds.intersect(selectedCatalogItemIds.toSet())
                }
            }

        val intersected = eligibleCatalogItemIds.intersect(option.catalogItemIds.toSet())
        if (intersected.isEmpty()) return true

        val matchingItems = model.catalogItems.filter { item ->
            item.catalogItemId()?.let(intersected::contains) == true
        }
        return matchingItems.isNotEmpty() && matchingItems.all { it.isOutOfStock() }
    }

    private fun resolveActiveCatalogItemIndex(
        model: LayoutSchemaUiModel.CatalogDropdownUiModel,
        selectedIndices: Map<Int, Int>,
    ): Int? {
        val group = model.catalogItemGroup ?: return null
        var candidateIds = group.catalogItemIds.toSet()
        selectedIndices.forEach { (attributeIndex, optionIndex) ->
            val option = group.attributes
                .getOrNull(attributeIndex)
                ?.options
                ?.getOrNull(optionIndex)
            option?.catalogItemIds?.let { optionCatalogItemIds ->
                candidateIds = candidateIds.intersect(optionCatalogItemIds.toSet())
            }
        }

        val matchingItems = model.catalogItems.mapIndexedNotNull { index, catalogItem ->
            val catalogItemId = catalogItem.catalogItemId()
            if (catalogItemId != null && candidateIds.contains(catalogItemId)) {
                index to catalogItem
            } else {
                null
            }
        }

        return matchingItems.firstOrNull { (_, catalogItem) -> !catalogItem.isOutOfStock() }?.first
            ?: matchingItems.firstOrNull()?.first
    }

    private fun CatalogItemModel.catalogItemId(): String? = properties.get(KEY_CATALOG_ITEM_ID)

    private fun CatalogItemModel.isOutOfStock(): Boolean =
        properties.get<String>(KEY_INVENTORY_STATUS).equals(OUT_OF_STOCK, ignoreCase = true)

    private fun selectionStateKey(attributeIndex: Int): String =
        "$CatalogDropdownCustomStateKeyPrefix$attributeIndex.selectedIndex"

    private data class ResolvedCatalogDropdownStyle(
        val style: LayoutSchemaUiModel.CatalogDropdownStyleUiModel?,
        val baseStyle: LayoutSchemaUiModel.CatalogDropdownStyleUiModel?,
    )

    private fun ResolvedCatalogDropdownStyle.resolvedPadding(
        breakpointIndex: Int,
        isPressed: Boolean,
    ): PaddingValues? {
        var padding: PaddingValues? = null

        fun applyPadding(properties: ModifierProperties?) {
            padding = padding ?: properties?.padding
        }

        fun applyPaddingFromModifierList(modifierProperties: List<StateBlock<ModifierProperties>>?) {
            for (iteration in 0..1) {
                val usePressed = isPressed && iteration == 0
                for (i in breakpointIndex downTo 0) {
                    modifierProperties?.getOrNull(i)?.let { stateBlock ->
                        applyPadding(if (usePressed) stateBlock.pressed else stateBlock.default)
                        if (usePressed && i == 0) {
                            applyPadding(stateBlock.default)
                        }
                    }
                }
                if (!usePressed) break
            }
        }

        applyPaddingFromModifierList(style?.ownModifiers)
        applyPaddingFromModifierList(baseStyle?.ownModifiers)
        return padding
    }

    private companion object {
        const val MIN_VISIBLE_OPTIONS = 2
        const val CatalogDropdownCustomStateKeyPrefix = "CatalogDropdown."
        const val KEY_CATALOG_ITEM_ID = "catalogItemId"
        const val KEY_INVENTORY_STATUS = "inventoryStatus"
        const val OUT_OF_STOCK = "OutOfStock"
        const val CollapsedIcon = "ExpandMore"
        const val ExpandedIcon = "ExpandLess"
        const val SelectedOptionMarker = "Check"
    }
}

internal enum class CatalogDropdownPopupPlacement {
    Above,
    Below,
}

internal fun catalogDropdownPopupPlacement(
    anchorBounds: IntRect,
    anchorHeight: Int,
    windowSize: IntSize,
    popupContentSize: IntSize,
): CatalogDropdownPopupPlacement {
    if (windowSize.height <= 0 || popupContentSize.height <= 0) {
        return CatalogDropdownPopupPlacement.Below
    }

    val anchorBottom = maxOf(anchorBounds.bottom, anchorBounds.top + anchorHeight)
    val spaceBelow = windowSize.height - anchorBottom
    val spaceAbove = anchorBounds.top
    val shouldShowBelow = spaceBelow >= popupContentSize.height || spaceBelow >= spaceAbove
    return if (shouldShowBelow) {
        CatalogDropdownPopupPlacement.Below
    } else {
        CatalogDropdownPopupPlacement.Above
    }
}

internal fun catalogDropdownPopupOffset(
    anchorBounds: IntRect,
    anchorHeight: Int,
    windowSize: IntSize,
    popupContentSize: IntSize,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): IntOffset {
    val x = if (layoutDirection == LayoutDirection.Ltr) {
        anchorBounds.left
    } else {
        anchorBounds.right - popupContentSize.width
    }
    val anchorBottom = maxOf(anchorBounds.bottom, anchorBounds.top + anchorHeight)
    val placement = catalogDropdownPopupPlacement(
        anchorBounds = anchorBounds,
        anchorHeight = anchorHeight,
        windowSize = windowSize,
        popupContentSize = popupContentSize,
    )
    val y = when (placement) {
        CatalogDropdownPopupPlacement.Below -> anchorBottom
        CatalogDropdownPopupPlacement.Above -> anchorBounds.top - popupContentSize.height
    }
    return IntOffset(x = x, y = y)
}

private val CatalogDropdownPopupPlacement.borderCornerRadiusMode: BorderCornerRadiusMode
    get() = when (this) {
        CatalogDropdownPopupPlacement.Above -> BorderCornerRadiusMode.Top
        CatalogDropdownPopupPlacement.Below -> BorderCornerRadiusMode.Bottom
    }

private class CatalogDropdownPopupPositionProvider(
    private val anchorHeight: Int,
    private val measuredAnchorBoundsInParent: IntRect?,
    private val windowOffset: IntOffset,
    private val onPlacementCalculated: (CatalogDropdownPopupPlacement) -> Unit,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val resolvedAnchorBounds = measuredAnchorBoundsInParent?.toWindowBounds(anchorBounds) ?: anchorBounds
        onPlacementCalculated(
            catalogDropdownPopupPlacement(
                anchorBounds = resolvedAnchorBounds,
                anchorHeight = anchorHeight,
                windowSize = windowSize,
                popupContentSize = popupContentSize,
            ),
        )
        return catalogDropdownPopupOffset(
            anchorBounds = resolvedAnchorBounds,
            anchorHeight = anchorHeight,
            windowSize = windowSize,
            popupContentSize = popupContentSize,
            layoutDirection = layoutDirection,
        ) + windowOffset
    }
}

private fun android.view.View.windowToScreenOffset(): IntOffset {
    val locationOnScreen = IntArray(2)
    val locationInWindow = IntArray(2)
    getLocationOnScreen(locationOnScreen)
    getLocationInWindow(locationInWindow)
    return IntOffset(
        x = locationOnScreen[0] - locationInWindow[0],
        y = locationOnScreen[1] - locationInWindow[1],
    )
}

private fun IntRect.toWindowBounds(parentBounds: IntRect): IntRect = IntRect(
    left = parentBounds.left + left,
    top = parentBounds.top + top,
    right = parentBounds.left + right,
    bottom = parentBounds.top + bottom,
)

private fun IntRect.relativeTo(parentBounds: IntRect): IntRect = IntRect(
    left = left - parentBounds.left,
    top = top - parentBounds.top,
    right = right - parentBounds.left,
    bottom = bottom - parentBounds.top,
)

private fun IntRect.inflate(padding: PaddingValues?, density: Density, layoutDirection: LayoutDirection): IntRect {
    if (padding == null) return this
    return with(density) {
        IntRect(
            left = left - padding.calculateLeftPadding(layoutDirection).roundToPx(),
            top = top - padding.calculateTopPadding().roundToPx(),
            right = right + padding.calculateRightPadding(layoutDirection).roundToPx(),
            bottom = bottom + padding.calculateBottomPadding().roundToPx(),
        )
    }
}

private fun LayoutCoordinates.boundsInWindowIntRect(): IntRect {
    val bounds = boundsInWindow()
    return IntRect(
        left = bounds.left.roundToInt(),
        top = bounds.top.roundToInt(),
        right = bounds.right.roundToInt(),
        bottom = bounds.bottom.roundToInt(),
    )
}
