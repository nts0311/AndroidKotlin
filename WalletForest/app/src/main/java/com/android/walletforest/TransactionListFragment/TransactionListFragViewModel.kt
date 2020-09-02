package com.android.walletforest.TransactionListFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Repository

class TransactionListFragViewModel(val repo: Repository) : ViewModel() {
    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var timeRange = TimeRange.MONTH

    private var transactionList: LiveData<List<Transaction>> = MediatorLiveData()

    private var _dataItemList = MediatorLiveData<List<DataItem>>()
        .apply {
            addSource(transactionList)
            {newList->

            }
        }


    fun setTimeRange(start: Long, end: Long, range: String) {
        startTime = start
        endTime = end
        timeRange = TimeRange.valueOf(range)

        transactionList = repo.getTransactionsBetweenRange(start, end)
    }
}