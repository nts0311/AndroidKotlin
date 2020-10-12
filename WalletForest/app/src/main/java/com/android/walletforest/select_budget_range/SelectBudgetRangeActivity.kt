package com.android.walletforest.select_budget_range

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.android.walletforest.dateToString
import com.android.walletforest.toEpochMilli
import com.android.walletforest.toLocalDate
import kotlinx.android.synthetic.main.activity_select_budget_range.*
import java.time.LocalDate

class SelectBudgetRangeActivity : AppCompatActivity() {

    private val rangeList = mutableListOf<BudgetRange>()
    private val budgetRangeAdapter = BudgetRangeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_budget_range)

        initList()
        setUpRangeList()
    }

    private fun initList() {
        val now = toLocalDate(System.currentTimeMillis())

        val startOfThisWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
        val endOfThisWeek = startOfThisWeek.plusWeeks(1).minusDays(1)
        addRange(getString(R.string.this_week), startOfThisWeek, endOfThisWeek)

        val startOfThisMonth = LocalDate.of(now.year, now.month, 1)
        val endOfThisMonth = startOfThisMonth.plusMonths(1).minusDays(1)
        addRange(getString(R.string.this_month), startOfThisMonth, endOfThisMonth)

        val startOfThisYear = LocalDate.of(now.year, 1, 1)
        val endOfThisYear = startOfThisYear.plusYears(1).minusDays(1)
        addRange(getString(R.string.this_year), startOfThisYear, endOfThisYear)

        val startOfNextWeek = startOfThisWeek.plusWeeks(1)
        val endOfNextWeek = startOfNextWeek.plusWeeks(1).minusDays(1)
        addRange(getString(R.string.next_week), startOfNextWeek, endOfNextWeek)

        val startOfNextMonth = startOfThisMonth.plusMonths(1)
        val endOfNextMonth = startOfNextMonth.plusMonths(1).minusDays(1)
        addRange(getString(R.string.next_month), startOfNextMonth, endOfNextMonth)

        val startOfNextYear = startOfThisYear.plusYears(1)
        val endOfNextYear = startOfNextYear.plusYears(1).minusDays(1)
        addRange(getString(R.string.next_year), startOfNextYear, endOfNextYear)

        addRange(getString(R.string.custom_range), now, now)
    }

    private fun addRange(title:String,startDate:LocalDate, endDate: LocalDate)
    {
        val rangeDetail =
            "${dateToString(startDate)} - ${dateToString(endDate)}"
        rangeList.add(BudgetRange(
            title,
            rangeDetail,
            startDate.toEpochMilli(),
            endDate.toEpochMilli()
        ))
    }

    private fun setUpRangeList()
    {
        budgetRangeAdapter.budgetRangeList = rangeList

        budgetRangeAdapter.rangeClickListener = {

        }

        budgetRangeAdapter.customRangeClickListener = {

        }

        range_list.adapter = budgetRangeAdapter
    }
}