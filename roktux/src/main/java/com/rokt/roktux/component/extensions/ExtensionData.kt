package com.rokt.roktux.component.extensions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
class ExtensionData(
    @SerialName("type")
    val type: String,
    @SerialName("name")
    val name: String,
    @SerialName("body")
    @Serializable(with = JsonAsStringSerializer::class)
    val body: String,
)

object JsonAsStringSerializer : JsonTransformingSerializer<String>(tSerializer = String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement = JsonPrimitive(value = element.toString())
}
