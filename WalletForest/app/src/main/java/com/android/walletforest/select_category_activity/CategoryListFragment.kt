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
import com.android.walletforest.model.repositories.Repository
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ARG_CATEGORY_TYPE = "category_type"
private const val ARG_ADD_ALL_CATEGORY = "add_all_category_type"


class CategoryListFragment : Fragment() {

    private var categoryType: String? = Constants.TYPE_EXPENSE
    private lateinit var viewModel: CategorySelectFragViewModel
    private var adapter = CategoryAdapter()
    private var currentList: List<Category> = listOf()
    private var filteredList: List<Category> = listOf()
    private var filterListJob: Job? = null
    private var addAllCategory = false

    var categoryClickListener: (category: Category) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryType = it.getString(ARG_CATEGORY_TYPE)

            if(it.containsKey(ARG_ADD_ALL_CATEGORY))
                addAllCategory = it.getBoolean(ARG_ADD_ALL_CATEGORY, false)
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

            if(addAllCategory)
            {
                val list = mutableListOf( Category(-1, -1, getString(R.string.all_categories),Constants.TYPE_EXPENSE,R.drawable.ic_category_all))
                list.addAll(it)
                adapter.categories = list
            }
            else
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
        fun newInstance(categoryType: String, addAllCategory : Boolean = false) =
            CategoryListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_TYPE, categoryType)
                    putBoolean(ARG_ADD_ALL_CATEGORY, addAllCategory)
                }
            }
    }
}