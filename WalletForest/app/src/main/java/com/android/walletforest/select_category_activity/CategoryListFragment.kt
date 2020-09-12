package com.android.walletforest.select_category_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Repository
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ARG_CATEGORY_TYPE = "category_type"


class CategoryListFragment : Fragment() {

    private var categoryType: String? = Constants.TYPE_EXPENSE
    private lateinit var viewModel: CategorySelectFragViewModel
    private var adapter = CategoryAdapter()
    private var currentList: List<Category> = listOf()
    private var filteredList: List<Category> = listOf()
    private var filterListJob: Job? = null

    var categoryClickListener: (category: Category) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryType = it.getString(ARG_CATEGORY_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vmFactory = RepoViewModelFactory(Repository.getInstance(requireContext()))

        viewModel = ViewModelProvider(requireActivity(), vmFactory)
            .get(categoryType!!, CategorySelectFragViewModel::class.java)

        viewModel.setCategoryType(categoryType!!)

        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.categories.observe(viewLifecycleOwner)
        {
            adapter.categories = it
            currentList = it
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            if (it == "")
                adapter.categories = currentList
            else
                filterCategoryList(it)
        }
    }

    private fun filterCategoryList(query: String) {
        filterListJob?.cancel()
        filterListJob = lifecycleScope.launch {
            withContext(Dispatchers.Default)
            {
                filteredList =
                    currentList.filter { category -> category.name.contains(query, true) }
            }

            adapter.categories = filteredList
        }
    }

    private fun setUpRecycleView() {
        category_rv.adapter = adapter
        adapter.categoryClickListener = categoryClickListener
        category_rv.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        @JvmStatic
        fun newInstance(categoryType: String) =
            CategoryListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_TYPE, categoryType)
                }
            }
    }
}