package com.rinfinity.cityfloassignment.socket

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rinfinity.cityfloassignment.ConnectionStatus
import com.rinfinity.cityfloassignment.model.TransactionItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val CONST_URL = "wss://ws.blockchain.info/inv"
private const val HUNDRED_MILLION = 100_000_000

class MySocketListener : WebSocketListener() {

    private val mConnectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String>
        get() = mConnectionStatus

    private val mClient = OkHttpClient()
    val client: OkHttpClient
        get() = mClient


    private val mTransactionItem = MutableLiveData<TransactionItemModel?>()
    val transactionItemModel: LiveData<TransactionItemModel?>
        get() = mTransactionItem

    private val mSubscribeJSON = "{\"op\":\"unconfirmed_sub\"}\n"
    private val mUnsubscribeJSON = JSONObject().apply { put("op", "unconfirmed_unsub") }

    private var mWebSocket: WebSocket? = null
    private var mRate: Double? = null
    fun initSocket(double: Double) {
        mRate = double
        mConnectionStatus.postValue(ConnectionStatus.CONNECTING)
        val request = Request.Builder().url(CONST_URL).build()
        mWebSocket = mClient.newWebSocket(request, this)
    }

    fun shutDownSocket() {
        mClient.dispatcher.executorService.shutdown()
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        mWebSocket?.send(mSubscribeJSON)
        mConnectionStatus.postValue(ConnectionStatus.CONNECTED)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val jsonObj = JSONObject(text)
                val x = jsonObj.getJSONObject("x")
                val hash = x.getString("hash")
                val time = x.getLong("time")
                var amount = 0.0
                val array = x.getJSONArray("out")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    amount += item.getDouble("value")
                }
                x.getJSONArray("out")
                amount /= HUNDRED_MILLION
                amount *= mRate!!
                if (amount >= 100) {
                    val date = Date(time * 1000L)
                    val simpleDateFormat = SimpleDateFormat("dd-mm-yyyy hh:mm:ss +05:30")
                    mTransactionItem.postValue(
                        TransactionItemModel(
                            amount = amount.toString(),
                            hash = hash,
                            time = simpleDateFormat.format(date).toString()
                        )
                    )
                }

            } catch (e: Exception) {
                Log.d(this.javaClass.simpleName, e.toString())
            }
        }

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        mConnectionStatus.postValue(ConnectionStatus.DIS_CONNECTED)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        mConnectionStatus.postValue(ConnectionStatus.DIS_CONNECTED)
        mWebSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        mConnectionStatus.postValue(ConnectionStatus.DIS_CONNECTED)
        mWebSocket = null

    }

}