package com.android.walletforest.TransactionsFragment

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Repository
import com.android.walletforest.viewpager2_fragment.TabFragmentStateAdapter
import com.android.walletforest.viewpager2_fragment.ViewPager2FragViewModel
import com.android.walletforest.viewpager2_fragment.ViewPager2Fragment


/*
class TransactionsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionsBinding
    lateinit var viewModel: TransactionsFragViewModel
    lateinit var viewPagerAdapter: TabFragmentStateAdapter
    private lateinit var tabLayout: TabLayout
    private var pagerPos = -1
    private lateinit var repo:Repository

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
                binding.mainViewPager.currentItem = pagerPos
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
}*/

class TransactionsFragment : ViewPager2Fragment()
{
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
        ).get(ViewPager2FragViewModel::class.java)

        viewPagerAdapter = TransactionsFragmentStateAdapter(this)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}