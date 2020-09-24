package com.android.walletforest.viewpager2_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.walletforest.R
import com.android.walletforest.databinding.FragmentViewpager2Binding
import com.android.walletforest.main_activity.MainActivity
import com.android.walletforest.report_fragment.ReportFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class ViewPager2Fragment : Fragment() {
    private lateinit var binding: FragmentViewpager2Binding
    private lateinit var tabLayout: TabLayout

    abstract var viewModel: ViewPager2FragViewModel
    abstract var viewPagerAdapter: TabFragmentStateAdapter

    private var pagePos = 0;

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            //binding.mainViewPager.setCurrentItem(pagePos, false)
        }
    }


    private fun setupObservers() {
        viewModel.tabInfoList.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewPagerAdapter.tabInfoList = it

                pagePos = it.size - 2

                /*if(this@ViewPager2Fragment is ReportFragment)
                {
                    binding.mainViewPager.post {
                        binding.mainViewPager.setCurrentItem(it.size - 2, false)
                    }
                }
                else*/
                    //binding.mainViewPager.setCurrentItem(it.size - 2, false)

                binding.mainViewPager.post {
                    binding.mainViewPager.setCurrentItem(it.size - 2, false)
                }

            }
        }

        viewModel.currentPage.observe(viewLifecycleOwner)
        {
            pagePos = it
        }

        viewModel.timeRange.observe(viewLifecycleOwner) {
            viewPagerAdapter.timeRange = it
        }
    }


    private fun setUpViewPager() {
        tabLayout = (requireActivity() as MainActivity).getTabLayout()
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        binding.apply {
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 2

            TabLayoutMediator(tabLayout, binding.mainViewPager, true) { tab, position ->
                tab.text = viewPagerAdapter.tabInfoList[position].tabTitle
            }.attach()

            //fix the weird error of viewpager2, where switch to another time range and back to month
            //caused it to shrink, no idea why lol

            binding.mainViewPager.updateLayoutParams {
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.i(
                        "aaa",
                        "${this@ViewPager2Fragment.javaClass.simpleName} - select $position"
                    )

                    //viewModel.setViewPagerPage(position)
                }
            })


        }
    }
}