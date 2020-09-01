package com.android.walletforest.TransactionsFragment

import androidx.lifecycle.*
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.walletforest.enums.TimeRange
import kotlinx.coroutines.async

class TransactionsFragViewModel(val repository: Repository) : ViewModel() {
    var currentWallet: LiveData<Wallet> = repository.getFirstWallet()
    var startTime: Long = 0L
    var endTime: Long = System.currentTimeMillis()
    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    private val tabInfoUtils = TabInfoUtils()

    var timeRange = TimeRange.MONTH
        set(value) {
            if (field.value == value.value)
                return
            field = value
            getTabInfoList()
        }

    var tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    init {
        startTime = TabInfoUtils.toEpoch(
            TabInfoUtils.toLocalDate(endTime).minusMonths(18)
        )

    }

    fun onCurrentWalletChange() {
        getTabInfoList()
    }

    private fun getTabInfoList() {
        if (currentWallet.value == null)
            return

        tabInfoUtils.setProperties(startTime, endTime, timeRange, currentWallet.value!!.id)

        viewModelScope.launch {
            val result = async {
                tabInfoUtils.getTabInfoList()
            }
            _tabInfoList.value = result.await()
        }
    }

}