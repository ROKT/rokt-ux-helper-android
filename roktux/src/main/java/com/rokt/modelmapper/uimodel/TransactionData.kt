package com.rokt.modelmapper.uimodel

data class TransactionData(
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
    val paymentType: String? = null,
    val supportedPaymentMethods: List<PaymentMethod>? = null,
    val isPartnerManagedPurchase: Boolean = true,
    val partnerPaymentReference: String? = null,
    val confirmationRef: String? = null,
    val metadata: Map<String, String> = emptyMap(),
)

data class PaymentMethod(val type: String)

data class Address(
    val name: String = "",
    val address1: String = "",
    val address2: String? = null,
    val city: String = "",
    val state: String = "",
    val stateCode: String = "",
    val country: String = "",
    val countryCode: String = "",
    val zip: String? = null,
)
