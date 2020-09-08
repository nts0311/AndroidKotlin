package com.android.walletforest.TransactionsFragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.databinding.FragmentTransactionsBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Repository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_transactions.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TransactionsFragment : Fragment() {

    lateinit var binding: FragmentTransactionsBinding
    lateinit var viewModel: TransactionsFragViewModel
    lateinit var viewPagerAdapter: TabFragmentStateAdapter

    var rangeSelectDialog: AlertDialog? = null

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

        setupObservers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setUpViewPager()
        createDialogs()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.transactions_frag_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.range_month_item -> viewModel.onTimeRangeChanged(TimeRange.MONTH)
            R.id.range_week_item -> viewModel.onTimeRangeChanged(TimeRange.WEEK)
            R.id.range_year_item -> viewModel.onTimeRangeChanged(TimeRange.YEAR)
            R.id.range_custom_item -> showRangeSelectDialog()
            R.id.switch_view_mode_item -> {
                val newViewMode = viewModel.switchViewMode()

                if (newViewMode == ViewType.TRANSACTION)
                    item.title = getString(R.string.view_by) + " " + getString(R.string.category)
                else
                    item.title = getString(R.string.view_by) + " " + getString(R.string.transaction)
            }
        }

        return true
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

        viewModel.currentWallet.observe(viewLifecycleOwner)
        {
            if (it != null) {
                binding.walletName.text = it.name
                binding.walletBalance.text = it.amount.toString()
                viewModel.onCurrentWalletChange()
            }
        }

        viewModel.categoryList.observe(viewLifecycleOwner)
        {
            if (it != null) {
                viewModel.updateCategories(it)
            }
        }
    }

    private fun setUpViewPager() {

        //dont use activity's fragManager here, it will result in crash
        viewPagerAdapter =
            TabFragmentStateAdapter(childFragmentManager, requireActivity().lifecycle)

        binding.apply {
            mainViewPager.adapter = viewPagerAdapter
            mainViewPager.offscreenPageLimit = 2
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        }

        TabLayoutMediator(binding.tabLayout, binding.mainViewPager) { tab, position ->
            tab.text = viewPagerAdapter.tabInfoList[position].tabTitle
        }.attach()


    }

    private fun showRangeSelectDialog() {
        rangeSelectDialog?.show()

        val startDate = TabInfoUtils.toLocalDate(viewModel.startTime)
        val endDate = TabInfoUtils.toLocalDate(viewModel.endTime)

        val startEdt = rangeSelectDialog?.findViewById<EditText>(R.id.start_time_edt)
        startEdt?.setText(dateToString(startDate))

        val endEdt = rangeSelectDialog?.findViewById<EditText>(R.id.end_time_edt)
        endEdt?.setText(dateToString(endDate))

        val startDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
            { _, year, monthOfYear, dayOfMonth ->
                val ld = LocalDate.of(year, monthOfYear, dayOfMonth)
                startEdt?.setText(dateToString(ld))
                startEdt?.tag = TabInfoUtils.toEpoch(ld)
            }

        val endDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
            { _, year, monthOfYear, dayOfMonth ->
                val ld = LocalDate.of(year, monthOfYear, dayOfMonth)
                endEdt?.setText(dateToString(ld))
                endEdt?.tag = TabInfoUtils.toEpoch(ld)
            }

        startEdt?.setOnClickListener {
            DatePickerDialog(
                requireContext(), startDateSetListener,
                startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
            ).show()
        }

        endEdt?.setOnClickListener {
            DatePickerDialog(
                requireContext(), endDateSetListener,
                endDate.year, endDate.monthValue - 1, endDate.dayOfMonth
            ).show()
        }

    }

    private fun dateToString(ld: LocalDate): String =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(ld)

    private fun createDialogs() {
        rangeSelectDialog = AlertDialog.Builder(requireContext()).run {

            val inflater = requireActivity().layoutInflater

            val dialogView = inflater.inflate(R.layout.range_selection_dialog, null)

            val startEdt = dialogView.findViewById<EditText>(R.id.start_time_edt)
            val endEdt = dialogView.findViewById<EditText>(R.id.end_time_edt)

            setView(dialogView)
            setPositiveButton(R.string.select_time) { dialog, id ->
                viewModel.onSelectCustomTimeRange(startEdt.tag as Long, endEdt.tag as Long)
            }

            setNegativeButton(R.string.cancel) { dialog, id ->
                dialog.cancel()
            }

            create()
        }
    }
}