package com.android.walletforest.TransactionsFragment

import android.util.Log
import androidx.lifecycle.*
import com.android.walletforest.TransactionListFragment.DataGrouper
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.async
import java.time.LocalDate

class TransactionsFragViewModel(private val repository: Repository) : ViewModel() {

    var currentWallet: LiveData<Wallet> = repository.getFirstWallet()


    var startTime: Long = 0L
        private set
    var endTime: Long = System.currentTimeMillis()
        private set

    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    private val tabInfoUtils = TabInfoUtils()
    private var timeRange = TimeRange.MONTH


    var tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    init {
        initTimeRange()
        test()
    }

    private fun initTimeRange() {

        val ld = TabInfoUtils.toLocalDate(endTime)
       /* endTime = TabInfoUtils.toEpoch(
            LocalDate.of(ld.year, ld.monthValue + 1, 1)
                .minusDays(1)
        )*/

        startTime = TabInfoUtils.toEpoch(
            LocalDate.of(ld.year, ld.monthValue, 1)
                .minusMonths(18)
        )

    }


    fun onCurrentWalletChange() {
        getTabInfoList()
    }

    fun onSelectCustomTimeRange(start: Long, end: Long) {
        if (startTime == start && endTime == end)
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.CUSTOM
        getTabInfoList()
    }

    //not include CUSTOM range
    fun onTimeRangeChanged(timeRange: TimeRange) {
        if (timeRange.value == this.timeRange.value)
            return
        this.timeRange = timeRange
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

    private fun test()
    {
        viewModelScope.launch {
            val transactions:MutableList<Transaction> = mutableListOf()

            //2-9
            transactions.add(Transaction
                (0,0,1, Constants.TYPE_EXPENSE,1000,"aaa",1599004800000))

            transactions.add(Transaction
                (1,1,1, Constants.TYPE_EXPENSE,1000,"aaa",1599004800000))

            transactions.add(Transaction
                (2,2,1, Constants.TYPE_EXPENSE,1000,"aaa",1599004800000))

            //5-9
            transactions.add(Transaction
                (3,2,1, Constants.TYPE_EXPENSE,1000,"aaa",1599264000000))

            transactions.add(Transaction
                (4,0,1, Constants.TYPE_EXPENSE,1000,"aaa",1599264000000))


            //13-9
            transactions.add(Transaction
                (5,1,1, Constants.TYPE_EXPENSE,1000,"aaa",1599955200000))

            transactions.add(Transaction
                (6,2,1, Constants.TYPE_EXPENSE,1000,"aaa",1599955200000))

            transactions.add(Transaction
                (7,0,1, Constants.TYPE_EXPENSE,1000,"aaa",1599955200000))

            transactions.add(Transaction
                (8,3,1, Constants.TYPE_EXPENSE,1000,"aaa",1599955200000))


            //24-9
            transactions.add(Transaction
                (9,3,1, Constants.TYPE_EXPENSE,1000,"aaa",1600905600000))

            //2-9
            transactions.add(Transaction
                (10,0,1, Constants.TYPE_EXPENSE,1000,"aaa",1599004800000))

            val grouper=DataGrouper()

            val result = async { grouper.doGrouping(transactions, TimeRange.YEAR, ViewType.TRANSACTION) }

            val data = result.await()

            Log.i("aaaa","aaaaa")
        }
    }

}