package com.android.walletforest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.android.walletforest.TransactionsFragment.TransactionsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
{

    val fragmentTransactions = TransactionsFragment()
    val fragmentReport=ReportFragment()
    val fragmentPlanning=PlanningFragment()
    var activeFragment : Fragment = fragmentTransactions


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val navController=findNavController(R.id.nav_host_fragment)
        //bottomNavigationView.setupWithNavController(navController)

        supportFragmentManager.apply {
            beginTransaction().add(R.id.fragment_container, fragmentPlanning, "frag_planning")
                .hide(fragmentPlanning).commit()
            beginTransaction().add(R.id.fragment_container, fragmentReport, "frag_report")
                .hide(fragmentReport).commit()
            beginTransaction().add(R.id.fragment_container, fragmentTransactions, "frag_transactions")
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.transactionsFragment ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentTransactions).commit()
                    activeFragment=fragmentTransactions
                    true
                }

                R.id.reportFragment ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentReport).commit()
                    activeFragment=fragmentReport
                    true
                }

                R.id.planningFragment ->{
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentPlanning).commit()
                    activeFragment=fragmentPlanning
                    true
                }

                else -> false
            }
        }
    }

}