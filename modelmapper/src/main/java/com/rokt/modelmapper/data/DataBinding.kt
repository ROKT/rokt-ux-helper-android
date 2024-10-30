package com.rokt.modelmapper.data

import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.uimodel.CreativeIcon
import com.rokt.modelmapper.uimodel.CreativeImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import com.rokt.modelmapper.utils.CURRENT_POSITION_PLACEHOLDER
import com.rokt.modelmapper.utils.TOTAL_OFFERS_PLACEHOLDER
import com.rokt.modelmapper.utils.transformToAnchorTag

interface DataBinding {
    fun bindValue(value: String, contextKey: String? = null, offerModel: OfferModel? = null): BindData
}

class DataBindingImpl : DataBinding {
    override fun bindValue(value: String, contextKey: String?, offerModel: OfferModel?): BindData =
        PlaceholderReplacer(value, contextKey, offerModel).bindData()
}

inline fun <reified T : Any> bindModel(inputKey: String, offerModel: OfferModel?): T? {
    return when (T::class) {
        ResponseOptionModel::class -> {
            offerModel?.creative?.responseOptions?.get(inputKey) as? T
        }

        CreativeImageModel::class -> {
            offerModel?.creative?.images?.get(inputKey) as? T
        }

        CreativeIcon::class -> {
            offerModel?.creative?.icons?.get(inputKey) as? T
        }

        else -> null
    }
}

private class PlaceholderReplacer(
    private val value: String,
    private val contextKey: String?,
    private val offer: OfferModel?,
) {
    fun bindData(): BindData {
        return try {
            if (isStateTemplate.matches(value)) {
                getStateData()
            } else {
                BindData.Value(templatePattern.replace(value, ::replacer))
            }
        } catch (e: Exception) {
            BindData.Undefined
        }
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

    private fun replacer(matchResult: MatchResult): String = reducer(removeIdentifiersAndSplit(matchResult.value))

    private fun reducer(keys: List<String>): String {
        var result: String? = null
        for (key in keys) {
            if (startsWithNamespace.containsMatchIn(key)) {
                if (isDataTemplate.containsMatchIn(key)) {
                    when (getNamespace(removePrefix(TemplateDataPrefix.DATA, key))) {
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
        matchResult.substring(2, matchResult.length - 2).split('|')

    private fun removePrefix(prefix: TemplateDataPrefix, key: String): String = key.removePrefix("${prefix.value}.")

    private fun getNamespace(key: String): String = key.substringBefore('.')

    private fun getSanitisedDataKey(key: String): String =
        key.removePrefix("${TemplateDataPrefix.DATA}.").substringAfter('.')

    private fun getCreativeCopy(key: String): String? {
        return offer?.creative?.copy?.get(getSanitisedDataKey(key))
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

private enum class TemplateDataPrefix(val value: String) {
    DATA("DATA"),
    STATE("STATE"),
}

private val startsWithNamespace = Regex("^(${TemplateDataPrefix.DATA}|${TemplateDataPrefix.STATE})")
private val isDataTemplate = Regex("^${TemplateDataPrefix.DATA}")
private val isStateTemplate = Regex("%\\^(${TemplateDataPrefix.STATE})\\.[a-zA-Z0-9]+[a-zA-Z0-9.]*(?:\\|.*?)?\\^%")
private val templatePattern = Regex(
    "%\\^(?:${TemplateDataPrefix.DATA}|${TemplateDataPrefix.STATE})\\.[a-zA-Z0-9]+[a-zA-Z0-9.]*(?:\\|.*?)?\\^%",
)

private const val CREATIVE_RESPONSE_NAMESPACE = "creativeResponse"
private const val CREATIVE_COPY_NAMESPACE = "creativeCopy"
private const val CREATIVE_LINKS_NAMESPACE = "creativeLink"
private val INDICATOR_POSITION = listOf("IndicatorPosition", "indicatorPosition")
private val TOTAL_OFFERS = listOf("TotalOffers", "totalOffers")
