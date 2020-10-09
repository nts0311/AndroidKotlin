package com.android.walletforest.TransactionListFragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.pie_chart_detail_activity.FilteringParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TransactionListFragViewModel(val repo: Repository) : ViewModel() {

    val viewMode = repo.viewMode

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    var currentWallet = repo.currentWallet
    var timeRange = TimeRange.MONTH
    var currentViewMode = ViewType.TRANSACTION
    private var dataGrouper = DataGrouper()

    private var groupDataJob: Job? = null

    var transactionList: LiveData<List<Transaction>> = MutableLiveData()

    private var _dataItemList: MutableLiveData<List<DataItem>> = MutableLiveData()
    var dataItemList: LiveData<List<DataItem>> = _dataItemList

    var previousWalletId = -1L

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
        groupDataJob?.cancel()

        groupDataJob = viewModelScope.launch {
            val result =
                async { dataGrouper.doGrouping(transactionList, timeRange, currentViewMode) }
            _dataItemList.value = result.await()
        }
    }

    override fun onCleared() {
        super.onCleared()
        groupDataJob?.cancel()
    }

    fun setTimeRange(
        start: Long,
        end: Long,
        range: String,
        walletId: Long,
        filteringParams: FilteringParams
    ) {

        if (startTime == start
            && endTime == end
            && range == timeRange.value
            && walletId == previousWalletId
        )
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.valueOf(range)

        val transactionsFlow = repo.getTransactionsBetweenRange(start, end, walletId)

        transactionList = if (filteringParams.categoryIdToFilter == -1L) transactionsFlow.asLiveData()
        else transactionsFlow.map {
            //include transactions with sub category in parent category
            val subCategoryId = mutableListOf<Long>()
            subCategoryId.add(filteringParams.categoryIdToFilter)

            if (!filteringParams.excludeSubCate)
                repo.categoryMap.values.forEach { category ->
                    if (category.parentId == filteringParams.categoryIdToFilter)
                        subCategoryId.add(category.id)
                }


            it.filter { transaction ->
                subCategoryId.contains(transaction.categoryId)
                        && transaction.type == filteringParams.transactionType
            }
        }
            .flowOn(Dispatchers.Default).asLiveData()

        previousWalletId = walletId
    }
}