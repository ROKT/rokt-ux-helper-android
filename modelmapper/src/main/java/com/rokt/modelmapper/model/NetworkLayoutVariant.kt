package com.rokt.modelmapper.model

import com.rokt.network.model.LayoutSchemaModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class NetworkLayoutVariant(
    @SerialName("layoutVariantId") val layoutVariantId: String,
    @SerialName("moduleName") val moduleName: String,
    @Serializable(with = NetworkLayoutSchemaSerializer::class)
    @SerialName("layoutVariantSchema")
    val layoutVariantSchema: LayoutSchemaModel, // DCUI Inner layout
)

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
        encoder.encodeSerializableValue(LayoutSchemaModel.serializer(), value)
    }
}
