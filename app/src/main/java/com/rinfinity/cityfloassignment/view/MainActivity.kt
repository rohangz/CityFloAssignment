package com.rinfinity.cityfloassignment.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rinfinity.cityfloassignment.view_model.MainActivityViewModel
import com.rinfinity.cityfloassignment.R
import com.rinfinity.cityfloassignment.adapter.TransactionListAdapter
import com.rinfinity.cityfloassignment.socket.MySocketListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mAdapter: TransactionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initList()
        initViewModel()
        initObservers()
        setOnClickListeners()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MainActivityViewModel.MainActivityViewModelFactory(application, MySocketListener())
        ).get(MainActivityViewModel::class.java)
        mViewModel.getBitcoinUSDRates()
    }

    private fun initObservers() {
        mViewModel.socketConnectionStatus.observe(this, Observer {
            app_status.text = it
        })
        mViewModel.transactionItemModel.observe(this, Observer {
            it?.let {
                mAdapter.addItemToList(it)
            }
        })
        mViewModel.rate.observe(this, Observer {
            if(it!=null) {
                initSocketConnection()
            } else {
                Toast.makeText(this, getString(R.string.error_msg)
                    , Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initSocketConnection() {
        mViewModel.initSocketConnection()
    }

    private fun initList() {
        mAdapter = TransactionListAdapter(ArrayList())
        app_list.adapter = mAdapter
        app_list.layoutManager = LinearLayoutManager(this)
    }

    private fun setOnClickListeners() {
        clear_list.setOnClickListener {
            mAdapter.clearList()
        }
    }

}