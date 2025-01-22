package com.rokt.modelmapper.model

import com.rokt.network.model.LayoutDisplayPreset
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutSettings
import com.rokt.network.model.RootSchemaModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

object RootSchemaModelSerializer :
    KSerializer<RootSchemaModel<LayoutSchemaModel, LayoutDisplayPreset, LayoutSettings>> {
    private val strategy = RootSchemaModel.serializer(
        LayoutSchemaModel.serializer(),
        LayoutDisplayPreset.serializer(),
        LayoutSettings.serializer(),
    )

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("RootSchemaModel", PrimitiveKind.STRING)

    override fun deserialize(
        decoder: Decoder,
    ): RootSchemaModel<LayoutSchemaModel, LayoutDisplayPreset, LayoutSettings> {
        val jsonString = decoder.decodeString()
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(strategy, jsonString)
    }

    override fun serialize(
        encoder: Encoder,
        value: RootSchemaModel<LayoutSchemaModel, LayoutDisplayPreset, LayoutSettings>,
    ) {
        val json = Json { ignoreUnknownKeys = true }
        encoder.encodeString(json.encodeToString(strategy, value))
    }
}
