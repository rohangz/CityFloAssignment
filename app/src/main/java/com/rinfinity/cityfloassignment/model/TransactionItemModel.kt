package com.rinfinity.cityfloassignment.model

import com.google.gson.annotations.SerializedName

data class TransactionItemModel(
    val hash: String,
    val amount: String,
    val time: String
)

data class RateConversionModelResponse(
    @SerializedName("bpi")
    val bpi: BPI
)


data class BPI(
    @SerializedName("USD")
    val usd: USD
)

data class USD(
    @SerializedName("code")
    val code: String,
    @SerializedName("rate")
    val rate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("rate_float")
    val rate_float: Double
)