package com.android.walletforest.add_transaction_fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.TransactionsFragment.TabInfoUtils
import com.android.walletforest.databinding.FragmentAddTransactionBinding
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Repository
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddTransactionFragment : Fragment() {

    private lateinit var binding : FragmentAddTransactionBinding
    private lateinit var viewModel : AddTransactionFragViewModel
    private var transactionId=-1L
    private var walletId=-1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_add_transaction, container, false)

        binding = FragmentAddTransactionBinding.inflate(inflater)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(requireContext().applicationContext))

        viewModel=ViewModelProvider(requireActivity(), vmFactory)
            .get(AddTransactionFragViewModel::class.java)

        getArgs()
        registerObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this){
            //findNavController().navigate(R.id.action_addTransactionFragment_to_transactionsFragment)
            //findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.addTranToolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_transaction_frag_menu, menu)
    }




    private fun getArgs()
    {
        /*//val args = AddTransactionFragmentArgs.fromBundle(requireArguments())
        transactionId = args.transactionId
        walletId = args.walletId*/

        if(transactionId != 1L)
            viewModel.setTransactionId(transactionId)
    }

    private fun dateToString(ld: LocalDate): String =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(ld)

    private fun registerObservers()
    {
        if(transactionId!=-1L)
        {
            viewModel.transaction.observe(viewLifecycleOwner)
            {
                if(it==null) return@observe

                //category
                val category = viewModel.categories[it.categoryId]
                binding.categoryImg.setImageResource(category!!.imageId)

                //amount text
                binding.amountTxt.text=it.amount.toString()
                val color = if (it.type == Constants.TYPE_EXPENSE)
                    ContextCompat.getColor(binding.root.context, R.color.expense_text)
                else
                    ContextCompat.getColor(binding.root.context, R.color.income_text)
                binding.amountTxt.setTextColor(color)
                binding.categoryTxt.text=category.name

                //date
                binding.dateTxt.text = dateToString(TabInfoUtils.toLocalDate(it.time))

                //wallet name
                binding.walletNameTxt.text = viewModel.wallets[it.walletId]?.name

            }
        }
    }

}











