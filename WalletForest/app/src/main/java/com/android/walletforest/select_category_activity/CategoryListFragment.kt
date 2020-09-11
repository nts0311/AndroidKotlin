package com.android.walletforest.select_category_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Repository


private const val ARG_CATEGORY_TYPE = "category_type"


class CategoryListFragment : Fragment() {

    private var categoryType: String? = Constants.TYPE_EXPENSE
    private lateinit var viewModel: CategorySelectFragViewModel

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

        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun registerObservers()
    {
        viewModel.categories.observe(viewLifecycleOwner)
        {

        }
    }

    fun setUpRecycleView()
    {

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