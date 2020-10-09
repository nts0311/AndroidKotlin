package com.android.walletforest.report_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.viewpager2_fragment.TabFragmentStateAdapter
import com.android.walletforest.viewpager2_fragment.ViewPager2FragViewModel
import com.android.walletforest.viewpager2_fragment.ViewPager2Fragment

class ReportFragment : ViewPager2Fragment() {

    override lateinit var viewModel: ViewPager2FragViewModel
    override lateinit var viewPagerAdapter: TabFragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repo = Repository.getInstance(requireContext().applicationContext)
        val vmFactory = RepoViewModelFactory(repo)
        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        ).get(ReportFragmentViewModel::class.java)

        viewPagerAdapter = ReportFragmentStateAdapter(this)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}