package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.network.model.CatalogCombinedCollectionLayoutSchemaTemplateNode
import com.rokt.network.model.CatalogCombinedCollectionModel
import com.rokt.network.model.CatalogImageGalleryModel
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

    @Test
    fun `transformCatalogImageGallery builds images from active catalog item`() {
        val gallery = LayoutSchemaModel.CatalogImageGallery(
            CatalogImageGalleryModel<WhenPredicate>(
                styles = null,
                showIndicators = false,
                backwardIcon = "chevron-left",
                forwardIcon = "chevron-right",
                a11yLabel = "Product images",
            ),
        )
        val offerModel = createOfferModel(
            catalogItemCount = 2,
            imageKeysByItem = mapOf(
                0 to listOf("catalogItemImage0"),
                1 to listOf("catalogItemImage2", "catalogItemImage1"),
            ),
        )

        val result = transformCatalogImageGallery(
            catalogImageGallery = gallery,
            offerModel = offerModel,
            itemIndex = 1,
            module = Module.AddToCart,
        )

        assertThat(result.images).containsOnlyKeys(0, 1)
        assertThat(result.images[0]?.lightUrl).isEqualTo("https://example.com/item-1-catalogItemImage1.png")
        assertThat(result.images[1]?.lightUrl).isEqualTo("https://example.com/item-1-catalogItemImage2.png")
        assertThat(result.showIndicators).isFalse()
        assertThat(result.backwardIcon).isEqualTo("chevron-left")
        assertThat(result.forwardIcon).isEqualTo("chevron-right")
        assertThat(result.a11yLabel).isEqualTo("Product images")
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

    private fun createOfferModel(
        catalogItemCount: Int,
        imageKeysByItem: Map<Int, List<String>> = emptyMap(),
    ): OfferModel = OfferModel(
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
                imageWrapper = CatalogImageWrapperModel(
                    HMap().apply {
                        imageKeysByItem[it]?.forEach { imageKey ->
                            set(TypedKey<OfferImageModel>(imageKey), createOfferImage(it, imageKey))
                        }
                    },
                ),
            )
        }.toImmutableList(),
    )

    private fun createOfferImage(catalogItemIndex: Int, imageKey: String): OfferImageModel = OfferImageModel(
        HMap().apply {
            set(TypedKey<String>(ExperienceModelMapperImpl.KEY_LIGHT), "https://example.com/item-$catalogItemIndex-$imageKey.png")
            set(TypedKey<String>(ExperienceModelMapperImpl.KEY_DARK), "")
            set(TypedKey<String>(ExperienceModelMapperImpl.KEY_ALT), "Catalog image")
            set(TypedKey<String>(ExperienceModelMapperImpl.KEY_TITLE), imageKey)
        },
    )
}
