package com.android.walletforest.transaction_list_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.android.walletforest.TransactionListFragment.*
import com.android.walletforest.enums.TimeRange

class TransactionListActivity : AppCompatActivity() {

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var walletId: Long? = null
    private var timeRange: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list2)

        setSupportActionBar(findViewById(R.id.toolbar2))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent?.let {
            startTime = it.getLongExtra(START_TIME_PARAM, 0L)
            endTime = it.getLongExtra(END_TIME_PARAM, 0L)
            walletId = it.getLongExtra(WALLET_ID_PARAM, 0L)
            timeRange = it.getStringExtra(TIME_RANGE_PARAM)
        }

        val transactionsFragment = TransactionListFragment.newInstance(
            startTime!!,
            endTime!!,
            walletId!!,
            TimeRange.valueOf(timeRange!!)
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.transactions_frag_container, transactionsFragment)
            .commit()
    }
}