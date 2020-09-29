package com.android.walletforest.viewpager2_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.walletforest.databinding.FragmentViewpager2Binding
import com.android.walletforest.main_activity.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

abstract class ViewPager2Fragment : Fragment() {
    private lateinit var binding: FragmentViewpager2Binding
    private lateinit var tabLayout: TabLayout
    private lateinit var tabLayoutMediator: TabLayoutMediator

    abstract var viewModel: ViewPager2FragViewModel
    abstract var viewPagerAdapter: TabFragmentStateAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewpager2Binding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setupObservers()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            tabLayoutMediator.attach()
            val pagePos = viewModel.getCurrentPage()
            binding.mainViewPager.setCurrentItem(pagePos, false)

            // Now update the scroll position to match the ViewPager's current item
            // post because of race condition
            tabLayout.post {
                tabLayout.setScrollPosition(pagePos, 0f, true)
            }
        } else
        {
            tabLayoutMediator.detach()
        }
    }


    private fun setupObservers() {
        viewModel.tabInfoList.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewPagerAdapter.tabInfoList = it
                binding.mainViewPager.setCurrentItem(it.size - 2, false)
            }
        }

        viewModel.timeRange.observe(viewLifecycleOwner) {
            viewPagerAdapter.timeRange = it
        }
    }


    private fun setUpViewPager() {

        tabLayout = (requireActivity() as MainActivity).getTabLayout(this)

        binding.apply {
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 2

            tabLayoutMediator = TabLayoutMediator(tabLayout, mainViewPager, true) { tab, position ->
                tab.text = viewPagerAdapter.tabInfoList[position].tabTitle
            }

            tabLayoutMediator.attach()

            //fix the weird error of viewpager2, where switch to another time range and back to month
            //caused it to shrink, no idea why lol

            mainViewPager.updateLayoutParams {
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setViewPagerPage(position)
                }
            })
        }
    }
}