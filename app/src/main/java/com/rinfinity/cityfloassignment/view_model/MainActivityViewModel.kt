package com.rinfinity.cityfloassignment.view_model

import android.app.Application
import androidx.lifecycle.*
import com.rinfinity.cityfloassignment.application.DemoApplication
import com.rinfinity.cityfloassignment.model.TransactionItemModel
import com.rinfinity.cityfloassignment.socket.MySocketListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivityViewModel(app: Application, private val mSocketListener: MySocketListener) :
    AndroidViewModel(app) {

    val socketConnectionStatus: LiveData<String> = mSocketListener.connectionStatus
    val transactionItemModel: LiveData<TransactionItemModel?> = mSocketListener.transactionItemModel

    private val mRate = MutableLiveData<Double>()
    val rate: LiveData<Double>
        get() = mRate


    fun getBitcoinUSDRates() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val url = "https://api.coindesk.com/v1/bpi/currentprice/USD.json"
                    val response =
                        getApplication<DemoApplication>().networkService.getBitcoinToUSDConversionRate(
                            url
                        )

                    val valueToPost: Double?
                    valueToPost = if (response.isSuccessful && response.body() != null) {
                        response.body()?.bpi?.usd?.rate_float
                    } else {
                        null
                    }
                    mRate.postValue(valueToPost)
                } catch (e: Exception) {
                    mRate.postValue(0.0)
                }
            }
        }

    }

    fun initSocketConnection() {
        mSocketListener.initSocket(mRate.value!!)
    }

    class MainActivityViewModelFactory(
        private val app: Application,
        private val mSocketListener: MySocketListener
    ) : ViewModelProvider.AndroidViewModelFactory(app) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
                return when (modelClass) {
                    MainActivityViewModel::class.java -> MainActivityViewModel(
                        app,
                        mSocketListener
                    ) as T
                    else -> throw Exception("Unable to create Instance of ${modelClass.javaClass.simpleName}")
                }
            }
            return super.create(modelClass)
        }
    }
}