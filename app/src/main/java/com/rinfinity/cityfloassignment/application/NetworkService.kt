package com.rinfinity.cityfloassignment.application

import com.rinfinity.cityfloassignment.model.RateConversionModelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NetworkService  {

    @GET
    suspend fun getBitcoinToUSDConversionRate(@Url url: String): Response<RateConversionModelResponse>
}