package com.android.walletforest.TransactionListFragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransactionListFragViewModel(val repo: Repository) : ViewModel() {

    val viewMode = repo.viewMode

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    var timeRange = TimeRange.MONTH
    var currentViewMode = ViewType.TRANSACTION
    private var dataGrouper = DataGrouper()

    var transactionList: LiveData<List<Transaction>> = MutableLiveData()

    private var _dataItemList: MutableLiveData<List<DataItem>> = MutableLiveData()
    var dataItemList: LiveData<List<DataItem>> = _dataItemList

    fun switchViewMode(viewType: ViewType) {
        if (currentViewMode == viewType) return

        currentViewMode = viewType
        if (transactionList.value != null)
            groupData(transactionList.value!!)
    }

    fun onTransactionListChange(transactionList: List<Transaction>) {
        groupData(transactionList)
    }

    private fun groupData(transactionList: List<Transaction>) {
        viewModelScope.launch {
            val result =
                async { dataGrouper.doGrouping(transactionList, timeRange, currentViewMode) }
            _dataItemList.value = result.await()
        }
    }


    fun setTimeRange(start: Long, end: Long, range: String) {

        startTime = start
        endTime = end
        timeRange = TimeRange.valueOf(range)

        transactionList = repo.getTransactionsBetweenRange(start, end)
    }
}