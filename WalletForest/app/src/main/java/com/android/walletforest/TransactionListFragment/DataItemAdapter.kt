package com.android.walletforest.TransactionListFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.databinding.FragmentTransactionsBinding
import com.android.walletforest.databinding.ItemTransactionBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction

class DataItemDiffCallbacks : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

private const val ITEM_HEADER = 0
private const val ITEM_DIVIDER = 1
private const val ITEM_TRANSACTION = 2


class DataItemAdapter(viewMode: ViewType, timeRange: TimeRange) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DataItemDiffCallbacks()) {
    var viewMode = viewMode
        set(value) {
            gViewMode = value
            field = value
            notifyDataSetChanged()
        }

    var timeRange = timeRange
        set(value) {
            gTimeRange = value
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

private var gViewMode: ViewType = ViewType.TRANSACTION
private var gTimeRange: TimeRange = TimeRange.MONTH

class TransactionItemViewHolder(binding: ItemTransactionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(transaction : Transaction)
    {

    }

    companion object {
        fun from(parent: ViewGroup): TransactionItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTransactionBinding.inflate(inflater)
            return TransactionItemViewHolder(binding)
        }
    }
}

























