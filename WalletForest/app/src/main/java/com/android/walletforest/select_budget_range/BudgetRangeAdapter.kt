package com.android.walletforest.select_budget_range

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R

class BudgetRangeAdapter : RecyclerView.Adapter<BudgetRangeViewHolder>() {

    var rangeClickListener: (BudgetRange) -> Unit = {}
    var customRangeClickListener: (BudgetRange) -> Unit = {}

    var budgetRangeList: List<BudgetRange> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetRangeViewHolder =
        BudgetRangeViewHolder.from(parent)

    override fun onBindViewHolder(holder: BudgetRangeViewHolder, position: Int) {
        if (position == budgetRangeList.size - 1)
            holder.bind(budgetRangeList[position], customRangeClickListener)
        else
            holder.bind(budgetRangeList[position], rangeClickListener)
    }

    override fun getItemCount(): Int = budgetRangeList.size
}

class BudgetRangeViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    private val titleText = root.findViewById<TextView>(R.id.title_txt)
    private val rangeDetailText = root.findViewById<TextView>(R.id.range_detail_txt)

    fun bind(budgetRange: BudgetRange, clickListener: (BudgetRange) -> Unit) {
        root.setOnClickListener {
            clickListener.invoke(budgetRange)
        }

        titleText.text = budgetRange.title
        rangeDetailText.text = budgetRange.rangeDetail
    }

    companion object {
        fun from(parent: ViewGroup): BudgetRangeViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemRoot = inflater.inflate(R.layout.item_budget_range, parent, false)
            return BudgetRangeViewHolder(itemRoot)
        }
    }
}