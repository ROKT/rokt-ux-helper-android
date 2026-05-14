package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.uimodel.Address
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupAttributeModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupOptionModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeModel
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.PaymentMethod
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.modelmapper.uimodel.WidthUiModel
import com.rokt.network.model.BackgroundStylingProperties
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.BasicTextModel
import com.rokt.network.model.CatalogCombinedCollectionLayoutSchemaTemplateNode
import com.rokt.network.model.CatalogCombinedCollectionModel
import com.rokt.network.model.CatalogDevicePayButtonElements
import com.rokt.network.model.CatalogDevicePayButtonModel
import com.rokt.network.model.CatalogDevicePayButtonStyles
import com.rokt.network.model.CatalogDropdownElements
import com.rokt.network.model.CatalogDropdownModel
import com.rokt.network.model.CatalogDropdownStyles
import com.rokt.network.model.CatalogImageGalleryElements
import com.rokt.network.model.CatalogImageGalleryModel
import com.rokt.network.model.CatalogImageGalleryStyles
import com.rokt.network.model.ColumnModel
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.DimensionHeightValue
import com.rokt.network.model.DimensionStylingProperties
import com.rokt.network.model.DimensionWidthValue
import com.rokt.network.model.FlexAlignment
import com.rokt.network.model.FlexJustification
import com.rokt.network.model.FormStateStylingBlock
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutStyle
import com.rokt.network.model.PaymentProvider
import com.rokt.network.model.TextStylingProperties
import com.rokt.network.model.ThemeColor
import com.rokt.network.model.ValidationTriggerConfig
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
                styles = LayoutStyle(
                    elements = CatalogImageGalleryElements(
                        own = emptyList(),
                        controlButton = listOf(
                            BasicStateStylingBlock(
                                default = CatalogImageGalleryStyles(
                                    text = TextStylingProperties(
                                        fontSize = 16f,
                                        textColor = ThemeColor(light = "#ffffff"),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
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
        assertThat(result.controlButton?.textStyles?.firstOrNull()?.default?.fontSize).isEqualTo(16f)
        assertThat(result.controlButton?.textStyles?.firstOrNull()?.default?.textColor?.light).isEqualTo("#ffffff")
        assertThat(result.a11yLabel).isEqualTo("Product images")
    }

    @Test
    fun `transformCatalogDevicePayButton maps provider children validation catalog item transaction data and styles`() {
        val transactionData = TransactionData(
            shippingAddress = Address(name = "Jane Shopper", countryCode = "US"),
            paymentType = "GooglePay",
            supportedPaymentMethods = listOf(PaymentMethod("GOOGLE_PAY")),
            isPartnerManagedPurchase = false,
            partnerPaymentReference = "partner-reference",
            confirmationRef = "confirmation-ref",
            metadata = mapOf("merchant" to "rokt"),
        )
        val offerModel = createOfferModel(
            catalogItemCount = 2,
            catalogItemPropertiesByItem = mapOf(
                1 to HMap().apply {
                    set(TypedKey<String>("catalogItemId"), "catalog-item-2")
                    set(TypedKey<String>("title"), "Weekend boots")
                },
            ),
            transactionData = transactionData,
        )
        val button = LayoutSchemaModel.CatalogDevicePayButton(
            CatalogDevicePayButtonModel<LayoutSchemaModel, WhenPredicate>(
                a11yLabel = "Pay securely",
                styles = LayoutStyle(
                    elements = CatalogDevicePayButtonElements(
                        own = listOf(
                            BasicStateStylingBlock(
                                default = CatalogDevicePayButtonStyles(
                                    container = ContainerStylingProperties(
                                        justifyContent = FlexJustification.Center,
                                        alignItems = FlexAlignment.Center,
                                    ),
                                    dimension = DimensionStylingProperties(
                                        width = DimensionWidthValue.Fixed(240f),
                                        height = DimensionHeightValue.Fixed(48f),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                provider = PaymentProvider.GooglePay,
                validatorTriggerConfig = ValidationTriggerConfig(
                    validatorFieldKeys = listOf("dropDownSelection", "quantity"),
                ),
                children = listOf(
                    LayoutSchemaModel.BasicText(
                        BasicTextModel(
                            styles = null,
                            value = "Ignored for branded Google Pay",
                        ),
                    ),
                ),
            ),
        )

        val result = transformCatalogDevicePayButton(button, offerModel, itemIndex = 1) {
            createTextUiModel("child")
        }

        assertThat(result.paymentProvider).isEqualTo(PaymentProvider.GooglePay)
        assertThat(result.children.single()?.text()).isEqualTo("child")
        assertThat(result.validatorFieldKeys).containsExactly("dropDownSelection", "quantity")
        assertThat(result.transactionData).isEqualTo(transactionData)
        assertThat(result.a11yLabel).isEqualTo("Pay securely")
        assertThat(result.catalogItemModel?.get<String>("catalogItemId")).isEqualTo("catalog-item-2")
        assertThat(result.catalogItemModel?.get<String>("title")).isEqualTo("Weekend boots")
        assertThat(result.ownModifiers?.first()?.default?.width).isEqualTo(WidthUiModel.Fixed(240f))
        assertThat(result.ownModifiers?.first()?.default?.height).isEqualTo(HeightUiModel.Fixed(48f))
    }

    @Test
    fun `transformCatalogDevicePayButton preserves every schema payment provider`() {
        val offerModel = createOfferModel(catalogItemCount = 1)

        PaymentProvider.values().forEach { provider ->
            val result = transformCatalogDevicePayButton(
                catalogDevicePayButton = createCatalogDevicePayButton(provider),
                offerModel = offerModel,
                itemIndex = 0,
            ) { null }

            assertThat(result.paymentProvider).isEqualTo(provider)
        }
    }

    private fun createCatalogDevicePayButton(provider: PaymentProvider): LayoutSchemaModel.CatalogDevicePayButton = LayoutSchemaModel.CatalogDevicePayButton(
        CatalogDevicePayButtonModel<LayoutSchemaModel, WhenPredicate>(
            provider = provider,
            children = emptyList(),
        ),
    )

    @Test
    fun `transformCatalogDropdown maps catalog item group and state styles`() {
        val dropdown = LayoutSchemaModel.CatalogDropdown(
            CatalogDropdownModel<WhenPredicate>(
                placeholderValue = "Select an option",
                unavailableValue = "Unavailable",
                styles = LayoutStyle(
                    elements = CatalogDropdownElements(
                        own = listOf(
                            FormStateStylingBlock(default = CatalogDropdownStyles()),
                        ),
                        head = listOf(
                            FormStateStylingBlock(
                                default = CatalogDropdownStyles(
                                    text = TextStylingProperties(
                                        fontSize = 14f,
                                        textColor = ThemeColor(light = "#111827"),
                                    ),
                                ),
                                selected = CatalogDropdownStyles(
                                    background = BackgroundStylingProperties(
                                        backgroundColor = ThemeColor(light = "#eef7ff"),
                                    ),
                                ),
                            ),
                        ),
                        option = listOf(
                            FormStateStylingBlock(
                                default = CatalogDropdownStyles(),
                                disabled = CatalogDropdownStyles(
                                    text = TextStylingProperties(
                                        textColor = ThemeColor(light = "#9ca3af"),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                a11yLabel = "Select size",
            ),
        )
        val offerModel = createOfferModel(
            catalogItemCount = 2,
            catalogItemGroup = createCatalogItemGroup(),
        )

        val result = transformCatalogDropdown(
            catalogDropdown = dropdown,
            offerModel = offerModel,
            attributeIndex = 0,
        )

        assertThat(result.placeholderValue).isEqualTo("Select an option")
        assertThat(result.unavailableValue).isEqualTo("Unavailable")
        assertThat(result.a11yLabel).isEqualTo("Select size")
        assertThat(result.attributeIndex).isEqualTo(0)
        assertThat(result.customStateKey).isEqualTo("CatalogDropdown.0.selectedIndex")
        assertThat(result.catalogItemGroup?.attributes?.single()?.label).isEqualTo("Select size")
        assertThat(result.catalogItemGroup?.attributes?.single()?.options?.map { it.label })
            .containsExactly("8oz", "16oz")
        assertThat(result.head?.default?.textStyles?.firstOrNull()?.default?.fontSize).isEqualTo(14f)
        assertThat(result.head?.selected?.ownModifiers?.firstOrNull()?.default?.backgroundColor?.light)
            .isEqualTo("#eef7ff")
        assertThat(result.option?.disabled?.textStyles?.firstOrNull()?.default?.textColor?.light)
            .isEqualTo("#9ca3af")
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
        catalogItemPropertiesByItem: Map<Int, HMap> = emptyMap(),
        transactionData: TransactionData? = null,
        catalogItemGroup: CatalogItemGroupModel? = null,
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
                properties = catalogItemPropertiesByItem[it] ?: HMap().apply {
                    set(TypedKey<String>("catalogItemId"), "catalog-item-$it")
                    set(TypedKey<String>("inventoryStatus"), "InStock")
                },
                imageWrapper = CatalogImageWrapperModel(
                    HMap().apply {
                        imageKeysByItem[it]?.forEach { imageKey ->
                            set(TypedKey<OfferImageModel>(imageKey), createOfferImage(it, imageKey))
                        }
                    },
                ),
            )
        }.toImmutableList(),
        transactionData = transactionData,
        catalogItemGroup = catalogItemGroup,
    )

    private fun createCatalogItemGroup(): CatalogItemGroupModel = CatalogItemGroupModel(
        groupId = "group-1",
        catalogItemIds = listOf("catalog-item-0", "catalog-item-1").toImmutableList(),
        attributes = listOf(
            CatalogItemGroupAttributeModel(
                attributeId = "size",
                label = "Select size",
                options = listOf(
                    CatalogItemGroupOptionModel(
                        label = "8oz",
                        catalogItemIds = listOf("catalog-item-0").toImmutableList(),
                        metadata = persistentMapOf(),
                    ),
                    CatalogItemGroupOptionModel(
                        label = "16oz",
                        catalogItemIds = listOf("catalog-item-1").toImmutableList(),
                        metadata = persistentMapOf(),
                    ),
                ).toImmutableList(),
                metadata = persistentMapOf(),
            ),
        ).toImmutableList(),
        metadata = persistentMapOf(),
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
