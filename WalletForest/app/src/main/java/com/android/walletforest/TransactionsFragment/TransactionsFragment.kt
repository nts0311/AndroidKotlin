package com.android.walletforest.TransactionsFragment

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.databinding.FragmentTransactionsBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.main_activity.MainActivity
import com.android.walletforest.model.Repository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TransactionsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionsBinding
    lateinit var viewModel: TransactionsFragViewModel
    lateinit var viewPagerAdapter: TabFragmentStateAdapter
    private lateinit var tabLayout: TabLayout
    private var pagerPos = -1
    private lateinit var repo:Repository


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("adapter", viewPagerAdapter.saveState())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentTransactionsBinding.inflate(inflater)

        repo = Repository.getInstance(requireContext().applicationContext)
        val vmFactory = RepoViewModelFactory(repo)

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        ).get(TransactionsFragViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.transactions_frag_menu, menu)
    }


    override fun onStop() {
        super.onStop()
        viewModel.tabLayoutPos = binding.mainViewPager.currentItem
    }

    private fun setupObservers() {
        viewModel.tabInfoList.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewPagerAdapter.tabInfoList = it

                //set the viewpager to the current month, week,...
                pagerPos = it.size - 2
                if (viewModel.tabLayoutPos != -1)
                    pagerPos = viewModel.tabLayoutPos

                if (it.size > 2)
                    binding.mainViewPager.currentItem = pagerPos
                //tabLayout.selectTab(tabLayout.getTabAt(pagerPos))
                else
                    binding.mainViewPager.currentItem = 0
            }
        }

        viewModel.timeRange.observe(viewLifecycleOwner) {
            viewPagerAdapter.timeRange = it
        }
    }

    private fun setUpViewPager() {
        tabLayout = (requireActivity() as MainActivity).getTabLayout()
        viewPagerAdapter =
            TabFragmentStateAdapter(this)

        binding.apply {
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 2

            TabLayoutMediator(tabLayout, binding.mainViewPager, true) { tab, position ->
                tab.text = viewPagerAdapter.tabInfoList[position].tabTitle
            }.attach()

            //fix the weird error of viewpager2, where switch to another time range and back to month
            //caused it to shrink, no idea why lol
            mainViewPager.updateLayoutParams {
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            tabLayout.tabMode = TabLayout.MODE_AUTO
        }
    }
}