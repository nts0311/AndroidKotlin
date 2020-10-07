package com.android.walletforest.planning_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.walletforest.R
import com.android.walletforest.budget_activity.BudgetActivity
import kotlinx.android.synthetic.main.fragment_planning.*

class PlanningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerClickListeners()
    }

    private fun registerClickListeners()
    {
        budget_button.setOnClickListener {
            val openBudgetIntent = Intent(requireContext(), BudgetActivity::class.java)
            startActivity(openBudgetIntent)
        }
    }
}