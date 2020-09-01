package com.android.walletforest.TransactionsFragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.databinding.FragmentTransactionsBinding
import com.android.walletforest.model.Repository
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_transactions.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TransactionsFragment : Fragment() {

    lateinit var binding: FragmentTransactionsBinding
    lateinit var viewModel: TransactionsFragViewModel
    lateinit var viewPagerAdapter:TabFragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentTransactionsBinding.inflate(inflater)

        val repo=Repository.getInstance(requireContext().applicationContext)
        val vmFactory=RepoViewModelFactory(repo)

        viewModel=ViewModelProvider(requireActivity(), vmFactory).get(TransactionsFragViewModel::class.java)

        setupObservers()
        //setUpViewPager()

        return binding.root
    }

    private fun setupObservers()
    {
        viewModel.tabInfoList.observe(viewLifecycleOwner)
        {
            if(it!=null)
                viewPagerAdapter.tabInfoList=it
        }
    }

    private fun setUpViewPager() {
        binding.mainViewPager.adapter=viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.mainViewPager){
            tab, position->
            tab.text=viewPagerAdapter.tabInfoList[position].tabTitle
        }.attach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        viewPagerAdapter = TabFragmentStateAdapter(parentFragmentManager, lifecycle)
        setUpViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.transactions_frag_menu, menu)
    }
}