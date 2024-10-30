package com.rokt.demoapp.ui.common

data class EditableField(
    val text: String = "",
    val onValueChanged: (String) -> Unit = {},
    val errorText: String = "",
)

data class EditableFieldSet(
    val order: Int = 0,
    val key: String = "",
    val value: String = "",
    val onKeyChanged: (String) -> Unit = {},
    val onValueChanged: (String) -> Unit = {},
)

fun createEditableField(text: String, onFieldEdited: (String) -> Unit, errorText: String = ""): EditableField {
    return EditableField(
        text = text,
        onValueChanged = fun(value: String) {
            onFieldEdited.invoke(value)
        },
        errorText = errorText,
    )
}
