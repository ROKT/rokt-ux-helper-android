package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.network.model.LayoutSchemaModel
import kotlinx.collections.immutable.toImmutableList

internal fun transformOverlay(
    overlayModel: LayoutSchemaModel.Overlay,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.OverlayUiModel {
    val ownStyles = overlayModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle ->
            ownStyle.toBasicStateStylingBlock { style -> style.container }
        },
    )

    val wrapperStyles = overlayModel.node.styles?.elements?.wrapper?.toImmutableList()
    val wrapperModifiers = wrapperStyles.transformModifier(
        transformBackground = { wrapperStyle -> wrapperStyle.toBasicStateStylingBlock { style -> style.background } },
        transformContainer = { wrapperStyle ->
            wrapperStyle.toBasicStateStylingBlock { style -> style.container }
        },
    )

    return LayoutSchemaUiModel.OverlayUiModel(
        ownModifiers = wrapperModifiers,
        containerProperties = wrapperStyles.transformContainer(
            transformContainer = { ownStyle ->
                ownStyle.toBasicStateStylingBlock { style -> style.container }
            },
        ),
        allowBackdropToClose = overlayModel.node.allowBackdropToClose,
        conditionalTransitionModifiers = null,
        child = LayoutSchemaUiModel.ColumnUiModel(
            ownModifiers,
            containerProperties = ownStyles.transformContainer(
                transformContainer = { ownStyle ->
                    ownStyle.toBasicStateStylingBlock { style -> style.container }
                },
                transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
            ),
            null,
            false,
            children = overlayModel.node.children.mapNotNull { child ->
                transformLayoutSchemaChildren(
                    child,
                )
            }.toImmutableList(),
        ),
    )
}

internal fun transformBottomSheet(
    bottomSheetModel: LayoutSchemaModel.BottomSheet,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.BottomSheetUiModel {
    val ownStyles = bottomSheetModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle ->
            ownStyle.toBasicStateStylingBlock { style -> style.container }
        },
    )?.map {
        it.copy(
            default = it.default.copy(borderUseTopCornerRadius = true),
            pressed = it.pressed?.copy(borderUseTopCornerRadius = true),
        )
    }?.toImmutableList()
    val wrapperStyles = bottomSheetModel.node.styles?.elements?.wrapper?.toImmutableList()
    val wrapperModifiers = wrapperStyles.transformModifier(
        transformBackground = { wrapperStyle -> wrapperStyle.toBasicStateStylingBlock { style -> style.background } },
        transformContainer = { wrapperStyle ->
            wrapperStyle.toBasicStateStylingBlock { style -> style.container }
        },
    )

    return LayoutSchemaUiModel.BottomSheetUiModel(
        ownModifiers = wrapperModifiers,
        containerProperties = wrapperStyles.transformContainer(
            transformContainer = { ownStyle ->
                ownStyle.toBasicStateStylingBlock { style -> style.container }
            },
        ),
        allowBackdropToClose = bottomSheetModel.node.allowBackdropToClose,
        conditionalTransitionModifiers = null,
        minimizable = bottomSheetModel.node.minimizable ?: false,
        child = LayoutSchemaUiModel.ColumnUiModel(
            ownModifiers,
            containerProperties = ownStyles.transformContainer(
                transformContainer = { ownStyle ->
                    ownStyle.toBasicStateStylingBlock { style -> style.container }
                },
                transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
            ),
            null,
            false,
            children = bottomSheetModel.node.children.mapNotNull { child ->
                transformLayoutSchemaChildren(
                    child,
                )
            }.toImmutableList(),
        ),
    )
}
