package com.android.walletforest.viewpager2_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.android.walletforest.R
import com.android.walletforest.databinding.FragmentViewpager2Binding
import com.android.walletforest.main_activity.MainActivity
import com.android.walletforest.report_fragment.ReportFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay

abstract class ViewPager2Fragment : Fragment() {
    private lateinit var binding: FragmentViewpager2Binding
    private lateinit var tabLayout: TabLayout

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.transactions_frag_menu, menu)
    }


    private fun setupObservers() {
        viewModel.tabInfoList.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewPagerAdapter.tabInfoList = it

                //set the viewpager to the current month, week,...
                Log.i("aaa", binding.mainViewPager.isAttachedToWindow.toString())
                binding.mainViewPager.setCurrentItem(it.size - 2, false)

            }
        }

        viewModel.timeRange.observe(viewLifecycleOwner) {
            viewPagerAdapter.timeRange = it
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            val s = "${this.javaClass.simpleName} - ${binding.mainViewPager.currentItem}"
            Log.i("aaa", s)

        } else {

        }
    }

    private fun setUpViewPager() {
        tabLayout = (requireActivity() as MainActivity).getTabLayout()

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

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
            {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.i("aaa", "${this@ViewPager2Fragment.javaClass.simpleName} - select $position")
                }
            })



            tabLayout.tabMode = TabLayout.MODE_AUTO
        }
    }
}