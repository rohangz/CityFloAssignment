package com.rinfinity.cityfloassignment.application

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var mApplication: DemoApplication

class DemoApplication: Application() {

    companion object {
        val instance: DemoApplication
            get() = mApplication
    }

    private lateinit var mNetworkService: NetworkService
    val networkService: NetworkService
        get() = mNetworkService
    override fun onCreate() {
        super.onCreate()
        mApplication = this
        mNetworkService = Retrofit.Builder().baseUrl("https://api.coindesk.com").addConverterFactory(
            GsonConverterFactory.create()
        ).build().create(NetworkService::class.java)
    }
}