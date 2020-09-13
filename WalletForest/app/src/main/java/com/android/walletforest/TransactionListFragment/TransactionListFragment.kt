package com.android.walletforest.TransactionListFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.add_transaction_activity.TransactionDetailActivity
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import kotlinx.android.synthetic.main.fragment_transaction_list.*


private const val START_TIME_PARAM = "startTime"
private const val END_TIME_PARAM = "endTime"
private const val WALLET_ID_PARAM = "walletId"
private const val TIME_RANGE_PARAM = "timeRange"

class TransactionListFragment : Fragment() {

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var walletId: Long? = null
    private var timeRange: String? = null
    private var itemAdapter: DataItemAdapter? = null
    private lateinit var repo: Repository
    var key = ""

    private lateinit var viewModel: TransactionListFragViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startTime = it.getLong(START_TIME_PARAM)
            endTime = it.getLong(END_TIME_PARAM)
            walletId = it.getLong(WALLET_ID_PARAM)
            timeRange = it.getString(TIME_RANGE_PARAM, TimeRange.MONTH.value)
            key = "$startTime - $endTime"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        repo = Repository.getInstance(requireContext().applicationContext)
        val vmFactory = RepoViewModelFactory(repo)

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        ).get(key, TransactionListFragViewModel::class.java)

        if (startTime != null && endTime != null && timeRange != null) {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!)
        }

        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        itemAdapter = DataItemAdapter(
            viewModel.currentViewMode,
            viewModel.timeRange,
            repo.categoryMap
        )
        transaction_list_rv.adapter = itemAdapter
        itemAdapter?.itemClickListener = {
            val detailIntent = Intent(requireContext(), TransactionDetailActivity::class.java)
            detailIntent.putExtra(TransactionDetailActivity.TRANSACTION_ID_PARAM, it.id)
            startActivity(detailIntent)
        }
    }

    private fun registerObservers() {
        viewModel.viewMode.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewModel.switchViewMode(it)
                itemAdapter?.viewMode = it
            }
        }

        viewModel.transactionList.observe(viewLifecycleOwner) {
            if (it != null)
                viewModel.onTransactionListChange(it)
        }

        //display transaction here
        viewModel.dataItemList.observe(viewLifecycleOwner) {
            if (it != null) {
                //copy to a brand new list to avoid ListAdapter not update the recyclerview
                itemAdapter?.submitList(it.toList())
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(startTime: Long, endTime: Long, walletId: Long, timeRange: TimeRange) =
            TransactionListFragment().apply {
                arguments = Bundle().apply {
                    putLong(START_TIME_PARAM, startTime)
                    putLong(END_TIME_PARAM, endTime)
                    putLong(WALLET_ID_PARAM, walletId)
                    putString(TIME_RANGE_PARAM, timeRange.value)
                }
            }
    }
}