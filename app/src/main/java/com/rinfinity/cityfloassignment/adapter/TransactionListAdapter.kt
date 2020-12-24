package com.rinfinity.cityfloassignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rinfinity.cityfloassignment.R
import com.rinfinity.cityfloassignment.model.TransactionItemModel
import kotlinx.android.synthetic.main.item_transaction.view.*

class TransactionListAdapter(private val mList: ArrayList<TransactionItemModel>) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionItemViewHolder>() {


    class TransactionItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(data: TransactionItemModel) {
            view.app_hash.text = "Hash ${data.hash}"
            view.app_amount.text = "Amount ${data.amount}"
            view.app_time.text = "Time ${data.time}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItemViewHolder =
        TransactionItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        )

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: TransactionItemViewHolder, position: Int) = holder.bindData(mList[position])

    fun addItemToList(item: TransactionItemModel) {
        if(mList.size < 5) {
            mList.add(item)
            notifyItemInserted(0)
        } else {
            mList.add(item)
            notifyItemInserted(0)
            mList.removeAt(mList.size-1)
            notifyItemRemoved(mList.size)
        }
    }

    fun clearList() {
        mList.clear()
        notifyDataSetChanged()
    }
}