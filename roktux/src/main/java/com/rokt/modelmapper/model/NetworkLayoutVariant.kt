package com.rokt.modelmapper.model

import com.rokt.network.model.LayoutSchemaModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

/**
 * Decodes/encodes a DCUI inner layout that arrives on the wire as a JSON **string**
 * into the renderer's typed [LayoutSchemaModel]. Used by `SelectLayoutVariant`.
 */
object NetworkLayoutSchemaSerializer : KSerializer<LayoutSchemaModel> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("NetworkLayoutRootSchema", PrimitiveKind.STRING)

    @Suppress("JSON_FORMAT_REDUNDANT")
    override fun deserialize(decoder: Decoder): LayoutSchemaModel {
        val jsonString = decoder.decodeString()
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(jsonString)
    }

    override fun serialize(encoder: Encoder, value: LayoutSchemaModel) {
        val json = Json { ignoreUnknownKeys = true }
        encoder.encodeString(json.encodeToString(value))
    }
}
