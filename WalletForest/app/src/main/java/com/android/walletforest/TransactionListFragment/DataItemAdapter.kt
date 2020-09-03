package com.android.walletforest.TransactionListFragment

import androidx.recyclerview.widget.DiffUtil

class DataItemDiffCallbacks : DiffUtil.ItemCallback<DataItem>()
{
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class DataItemAdapter
{
}