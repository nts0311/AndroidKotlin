package com.android.walletforest.budget_list_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.Repository

private const val BUDGET_STATUS = "budget_status"
private const val BUDGET_STATUS_RUNNING = "budget_status_running"
private const val BUDGET_STATUS_ENDED = "budget_status_ended"


class BudgetListFragment : Fragment() {

    private var budgetsStatus: String = BUDGET_STATUS_RUNNING
    private lateinit var viewModel: BudgetListFragViewModel

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

        viewModel.budgetList.observe(viewLifecycleOwner)
        {

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