package com.android.walletforest.budget_list_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.budget_detail_activity.BudgetDetailActivity
import com.android.walletforest.model.repositories.Repository
import kotlinx.android.synthetic.main.fragment_budget_list.*

const val BUDGET_STATUS = "budget_status"
const val BUDGET_STATUS_RUNNING = "budget_status_running"
const val BUDGET_STATUS_ENDED = "budget_status_ended"


class BudgetListFragment : Fragment() {

    private var budgetsStatus: String = BUDGET_STATUS_RUNNING
    private lateinit var viewModel: BudgetListFragViewModel
    private val budgetAdapter = BudgetAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            budgetsStatus = it.getString(BUDGET_STATUS, BUDGET_STATUS_RUNNING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val repository = Repository.getInstance(requireContext().applicationContext)
        val vmFactory = RepoViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), vmFactory).get(
            budgetsStatus,
            BudgetListFragViewModel::class.java
        )

        viewModel.filterRunningBudgets = (budgetsStatus == BUDGET_STATUS_RUNNING)

        return inflater.inflate(R.layout.fragment_budget_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()

        viewModel.budgetList.observe(viewLifecycleOwner)
        {
            budgetAdapter.budgetList = it
        }

        viewModel.currentWallet.observe(viewLifecycleOwner)
        {
            viewModel.onWalletChanged(it.id)
        }
    }

    private fun setUpRecycleView()
    {
        budget_list_rv.layoutManager = LinearLayoutManager(requireContext())
        budget_list_rv.adapter = budgetAdapter
        budgetAdapter.budgetClickListener = {
            val startBudgetDetailActivity = Intent(this@BudgetListFragment.context, BudgetDetailActivity::class.java)
            startBudgetDetailActivity.putExtra(BudgetDetailActivity.BUDGET_ID_KEY, it.id)
            startActivity(startBudgetDetailActivity)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(budgetsStatus: String) =
            BudgetListFragment().apply {
                arguments = Bundle().apply {
                    putString(BUDGET_STATUS, budgetsStatus)
                }
            }
    }
}