package com.android.walletforest.TransactionsFragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.databinding.FragmentTransactionsBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.main_activity.MainActivity
import com.android.walletforest.model.Repository
import com.android.walletforest.select_category_activity.SelectCategoryActivity
import com.android.walletforest.toEpoch
import com.android.walletforest.toLocalDate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    lateinit var viewModel: TransactionsFragViewModel
    lateinit var viewPagerAdapter: TabFragmentStateAdapter



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

        val repo = Repository.getInstance(requireContext().applicationContext)
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
                binding.mainViewPager.setCurrentItem(it.size - 2, true)
            }
        }

        viewModel.timeRange.observe(viewLifecycleOwner){
            viewPagerAdapter.timeRange = it
        }
    }

    private fun setUpViewPager() {

        //dont use activity's fragManager here, it will result in crash

        val tabLayout = (requireActivity() as MainActivity).getTabLayout()


        viewPagerAdapter =
            TabFragmentStateAdapter(childFragmentManager, lifecycle)

        binding.apply {
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 2

            //fix the weird error of viewpager2, where switch to another time range and back to month
            //caused it to shrink, no idea why lol
            mainViewPager.updateLayoutParams {
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        }

        TabLayoutMediator(tabLayout, binding.mainViewPager) { tab, position ->
            tab.text = viewPagerAdapter.tabInfoList[position].tabTitle
        }.attach()
    }


}