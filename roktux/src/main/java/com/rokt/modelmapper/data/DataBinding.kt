package com.rokt.modelmapper.data

import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.uimodel.Address
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeIcon
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.modelmapper.utils.CURRENT_POSITION_PLACEHOLDER
import com.rokt.modelmapper.utils.TOTAL_OFFERS_PLACEHOLDER
import com.rokt.modelmapper.utils.transformToAnchorTag
import java.util.TreeMap

interface DataBinding {
    fun bindValue(value: String, contextKey: String? = null, offerModel: OfferModel?, itemIndex: Int = 0): BindData
}

class DataBindingImpl : DataBinding {
    override fun bindValue(value: String, contextKey: String?, offerModel: OfferModel?, itemIndex: Int): BindData =
        PlaceholderReplacer(value, contextKey, offerModel, itemIndex).bindData()
}

internal inline fun <reified T : Any> bindModel(
    inputKey: String = "",
    offerModel: OfferModel?,
    module: Module = Module.StandardMarketing,
    itemIndex: Int = 0,
): T? = when (T::class) {
    ResponseOptionModel::class -> {
        offerModel?.creative?.responseOptions?.get(inputKey) as? T
    }

    OfferImageModel::class ->
        inputKey.split('|').firstNotNullOfOrNull { subKey ->
            val trimmedKey = subKey.trim()
            if (module == Module.AddToCart) {
                offerModel?.catalogItems?.getOrNull(itemIndex)
                    ?.imageWrapper?.properties?.get<OfferImageModel>(TypedKey<OfferImageModel>(trimmedKey))
            } else {
                offerModel?.creative?.images?.get(trimmedKey)
            }
        } as? T

    CreativeIcon::class -> {
        offerModel?.creative?.icons?.get(inputKey) as? T
    }

    CatalogItemModel::class -> {
        offerModel?.catalogItems?.getOrNull(itemIndex) as? T
    }

    else -> null
}

private class PlaceholderReplacer(
    private val value: String,
    private val contextKey: String?,
    private val offer: OfferModel? = null,
    private val itemIndex: Int,
) {
    fun bindData(): BindData = try {
        if (isStateTemplate.matches(value)) {
            getStateData()
        } else {
            BindData.Value(templatePattern.replace(value, ::replacer))
        }
    } catch (e: Exception) {
        BindData.Undefined
    }

    private fun getStateData(): BindData {
        var bindData: BindData = BindData.Undefined
        isStateTemplate.matchEntire(value)?.let { matchResult ->
            val keys = removeIdentifiersAndSplit(matchResult.value)
            for (key in keys) {
                if (startsWithNamespace.containsMatchIn(key)) {
                    when (removePrefix(TemplateDataPrefix.STATE, key)) {
                        in INDICATOR_POSITION -> {
                            bindData = BindData.State(BindState.OFFER_POSITION)
                            break
                        }

                        in TOTAL_OFFERS -> {
                            bindData = BindData.Value(TOTAL_OFFERS_PLACEHOLDER)
                            break
                        }
                    }
                }
            }
        }
        return bindData
    }

    private fun replacer(matchResult: MatchResult): String = reducer(
        keys = removeIdentifiersAndSplit(matchResult.value),
        rawToken = matchResult.value,
    )

    private fun reducer(keys: List<String>, rawToken: String): String {
        var result: String? = null
        for (key in keys) {
            if (startsWithNamespace.containsMatchIn(key)) {
                if (isDataTemplate.containsMatchIn(key)) {
                    when (getNamespace(removePrefix(TemplateDataPrefix.DATA, key))) {
                        CATALOG_RUNTIME_NAMESPACE -> {
                            // Keep placeholder token for runtime resolution at rendering stage.
                            result = rawToken
                        }

                        CREATIVE_COPY_NAMESPACE -> {
                            val copyVal = getCreativeCopy(key)
                            if (copyVal != null) {
                                result = copyVal
                            }
                        }

                        CREATIVE_LINKS_NAMESPACE -> {
                            result = getLinkData(key)
                        }

                        CREATIVE_RESPONSE_NAMESPACE -> {
                            val responseVal = getResponseOptionData(key)
                            if (responseVal != null) {
                                result = responseVal
                            }
                        }

                        CREATIVE_IMAGE_NAMESPACE -> {
                            val sanitisedKey = getSanitisedDataKey(key)
                            val imageVal = offer?.creative?.images?.get(sanitisedKey.substringBeforeLast('.'))
                            if (imageVal != null) {
                                result =
                                    imageVal.properties.get<String>(
                                        TypedKey<String>(sanitisedKey.substringAfterLast('.')),
                                    )
                            }
                        }

                        CATALOG_ITEM_NAMESPACE -> {
                            val catalogItemCopy = getCatalogItemCopy(key, itemIndex)
                            if (catalogItemCopy != null) {
                                result = catalogItemCopy
                            }
                        }

                        TRANSACTION_DATA_NAMESPACE -> {
                            val txnVal = getTransactionData(key)
                            if (txnVal != null) {
                                result = txnVal
                            }
                        }

                        else -> {}
                    }
                } else {
                    when (removePrefix(TemplateDataPrefix.STATE, key)) {
                        in INDICATOR_POSITION -> {
                            result = CURRENT_POSITION_PLACEHOLDER
                        }

                        in TOTAL_OFFERS -> {
                            result = TOTAL_OFFERS_PLACEHOLDER
                        }
                    }
                }
            } else {
                result = key
            }
            if (result != null) {
                break
            }
        }
        return result ?: throw IllegalArgumentException("Unable to parse $value")
    }

    private fun removeIdentifiersAndSplit(matchResult: String): List<String> =
        matchResult.substring(2, matchResult.length - 2).split('|').map { it.trim() }

    private fun removePrefix(prefix: TemplateDataPrefix, key: String): String = key.removePrefix("${prefix.value}.")

    private fun getNamespace(key: String): String = key.substringBefore('.')

    private fun getSanitisedDataKey(key: String): String =
        key.removePrefix("${TemplateDataPrefix.DATA}.").substringAfter('.')

    private fun getCreativeCopy(key: String): String? = offer?.creative?.copy?.get(getSanitisedDataKey(key))

    private fun getCatalogItemCopy(key: String, itemIndex: Int): String? {
        val catalogItem = offer?.catalogItems?.getOrNull(itemIndex) ?: return null
        val path = getSanitisedDataKey(key)
        if (path.startsWith(CATALOG_ITEM_IMAGES_PREFIX)) {
            return resolveCatalogItemImageField(catalogItem, path.removePrefix(CATALOG_ITEM_IMAGES_PREFIX))
        }
        if (path.startsWith(CATALOG_ITEM_COPY_PREFIX)) {
            return catalogItem.copy[path.removePrefix(CATALOG_ITEM_COPY_PREFIX)]
        }
        return catalogItem.properties.get<String>(TypedKey<String>(path))
    }

    private fun getTransactionData(key: String): String? {
        val transactionData = offer?.transactionData ?: return null
        val segments = getSanitisedDataKey(key).split('.')
        return when (segments.firstOrNull()) {
            "shippingAddress" -> resolveAddressField(transactionData.shippingAddress, segments.drop(1))
            "billingAddress" -> resolveAddressField(transactionData.billingAddress, segments.drop(1))
            "confirmationRef" -> transactionData.confirmationRef
            "paymentType" -> transactionData.paymentType
            "partnerPaymentReference" -> transactionData.partnerPaymentReference
            else -> null
        }
    }

    private fun resolveAddressField(address: Address?, rest: List<String>): String? {
        if (address == null || rest.isEmpty()) return null
        return when (rest.first()) {
            "name" -> address.name
            "address1" -> address.address1
            "address2" -> address.address2
            "city" -> address.city
            "state" -> address.state
            "stateCode" -> address.stateCode
            "country" -> address.country
            "countryCode" -> address.countryCode
            "zip" -> address.zip
            else -> null
        }
    }

    /**
     * Resolves `images.<slot>.<leaf>` against [CatalogItemModel.imageWrapper], mirroring nested
     * struct navigation for placeholders such as `%^DATA.catalogItem.images.catalogItemImage2.light^%`.
     * When the path stops at the image slot (no leaf), returns `""` so downstream logic treats
     * the value as present-but-empty (parity with iOS catalog extractor).
     */
    private fun resolveCatalogItemImageField(catalogItem: CatalogItemModel, pathAfterImages: String): String? {
        if (pathAfterImages.isBlank()) return null
        val segments = pathAfterImages.split('.')
        val slotKey = segments.firstOrNull() ?: return null
        val imageModel = catalogItem.imageWrapper.properties.get<OfferImageModel>(TypedKey<OfferImageModel>(slotKey))
            ?: return null
        if (segments.size == 1) {
            return ""
        }
        val leafKey = segments.drop(1).joinToString(".")
        return imageModel.properties.get<String>(TypedKey<String>(leafKey))
    }

    private fun getResponseOptionData(key: String): String? {
        offer?.creative?.responseOptions?.get(contextKey.orEmpty())?.let {
            val sanitizedKey = getSanitisedDataKey(key)
            return it.properties[TypedKey<String>(sanitizedKey)]
        }
        return null
    }

    private fun getLinkData(key: String): String? =
        offer?.creative?.links?.get(getSanitisedDataKey(key))?.transformToAnchorTag()
}

internal fun getOfferImages(inputKey: String = "", offerModel: OfferModel?): Map<Int, OfferImageModel> {
    val prefixes = inputKey.split('|').map { it.trim() }.filter(String::isNotEmpty)
    val images = offerModel?.creative?.images ?: return emptyMap()

    return images.mapNotNull { (imageKey, imageValue) ->
        val matchingPrefix = prefixes.find { prefix -> imageKey.startsWith(prefix) }
        if (matchingPrefix != null && imageKey.length > matchingPrefix.length) {
            val suffix = imageKey.substring(matchingPrefix.length + 1)
            suffix.toIntOrNull()?.let { imageNumber ->
                imageNumber to imageValue
            }
        } else {
            null
        }
    }.toMap(TreeMap())
}

internal fun getCatalogItemImages(offerModel: OfferModel?, itemIndex: Int, module: Module): Map<Int, OfferImageModel> {
    val catalogItemIndex = if (module == Module.AddToCart) itemIndex else 0
    val images = offerModel?.catalogItems?.getOrNull(catalogItemIndex)?.imageWrapper?.properties?.map
        ?: return emptyMap()

    return images.entries
        .mapNotNull { (key, value) ->
            (value as? OfferImageModel)?.let { image -> key.key to image }
        }
        .sortedWith(
            compareBy<Pair<String, OfferImageModel>> { (key, _) -> key.trailingNumber() ?: Int.MAX_VALUE }
                .thenBy { (key, _) -> key },
        )
        .mapIndexed { index, (_, image) -> index to image }
        .toMap()
}

private fun String.trailingNumber(): Int? = Regex("(\\d+)$").find(this)?.value?.toIntOrNull()

private val startsWithNamespace = Regex("^(${TemplateDataPrefix.DATA}|${TemplateDataPrefix.STATE})")
private val isDataTemplate = Regex("^${TemplateDataPrefix.DATA}")
private val isStateTemplate = Regex("%\\^(${TemplateDataPrefix.STATE})\\.[a-zA-Z0-9]+[a-zA-Z0-9.]*(?:\\|.*?)?\\^%")
private val templatePattern = Regex(
    "%\\^(?:${TemplateDataPrefix.DATA}|${TemplateDataPrefix.STATE})\\.[a-zA-Z0-9]+[a-zA-Z0-9.]*(?:\\s*\\|.*?)?\\^%",
)

private const val CATALOG_ITEM_IMAGES_PREFIX = "images."
private const val CATALOG_ITEM_COPY_PREFIX = "copy."
private const val CATALOG_RUNTIME_NAMESPACE = "catalogRuntime"

private val INDICATOR_POSITION = listOf("IndicatorPosition", "indicatorPosition")
private val TOTAL_OFFERS = listOf("TotalOffers", "totalOffers")
