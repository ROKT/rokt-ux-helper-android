package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.network.model.CatalogCombinedCollectionLayoutSchemaTemplateNode
import com.rokt.network.model.CatalogCombinedCollectionModel
import com.rokt.network.model.ColumnModel
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.WhenPredicate
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ContainerModelMapperTest {

    @Test
    fun `transformCatalogCombinedCollection builds one template per catalog item`() {
        val collection = LayoutSchemaModel.CatalogCombinedCollection(
            CatalogCombinedCollectionModel<CatalogCombinedCollectionLayoutSchemaTemplateNode, WhenPredicate>(
                styles = null,
                template = CatalogCombinedCollectionLayoutSchemaTemplateNode.Column(
                    ColumnModel<LayoutSchemaModel, WhenPredicate>(
                        styles = null,
                        children = emptyList(),
                    ),
                ),
            ),
        )
        val offerModel = createOfferModel(catalogItemCount = 2)
        val calls = mutableListOf<Pair<Int, Module>>()

        val result = transformCatalogCombinedCollection(collection, offerModel) { index, module, _ ->
            calls += index to module
            createTextUiModel("item-$index")
        }

        assertThat(calls).containsExactly(0 to Module.AddToCart, 1 to Module.AddToCart)
        assertThat(result.childrenByCatalogItem).containsOnlyKeys(0, 1)
        assertThat(result.childrenByCatalogItem[0]?.single()?.text()).isEqualTo("item-0")
        assertThat(result.childrenByCatalogItem[1]?.single()?.text()).isEqualTo("item-1")
    }

    private fun createTextUiModel(value: String): LayoutSchemaUiModel.BasicTextUiModel = LayoutSchemaUiModel.BasicTextUiModel(
        ownModifiers = null,
        containerProperties = null,
        conditionalTransitionModifiers = null,
        conditionalTransitionTextStyling = null,
        textStyles = null,
        value = BindData.Value(value),
    )

    private fun LayoutSchemaUiModel.text(): String? = (this as? LayoutSchemaUiModel.BasicTextUiModel)?.value
        ?.let { value -> (value as? BindData.Value)?.text }

    private fun createOfferModel(catalogItemCount: Int): OfferModel = OfferModel(
        campaignId = "campaignId",
        creative = CreativeModel(
            referralCreativeId = "creativeId",
            instanceGuid = "creativeInstanceGuid",
            token = "creativeToken",
            responseOptions = persistentMapOf(),
            copy = persistentMapOf(),
            images = persistentMapOf(),
            links = persistentMapOf(),
            icons = persistentMapOf(),
        ),
        catalogItems = List(catalogItemCount) {
            CatalogItemModel(
                properties = HMap(),
                imageWrapper = CatalogImageWrapperModel(HMap()),
            )
        }.toImmutableList(),
    )
}
