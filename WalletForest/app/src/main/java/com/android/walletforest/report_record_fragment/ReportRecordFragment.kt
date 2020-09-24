package com.android.walletforest.report_record_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.walletforest.R
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.toLocalDate
import kotlinx.android.synthetic.main.fragment_report_record.*

private const val START_TIME_PARAM = "startTime"
private const val END_TIME_PARAM = "endTime"
private const val WALLET_ID_PARAM = "walletId"
private const val TIME_RANGE_PARAM = "timeRange"

class ReportRecordFragment : Fragment() {
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var walletId: Long? = null
    private var timeRange: String? = null
    var viewModelKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startTime = it.getLong(START_TIME_PARAM)
            endTime = it.getLong(END_TIME_PARAM)
            walletId = it.getLong(WALLET_ID_PARAM)
            timeRange = it.getString(TIME_RANGE_PARAM, TimeRange.MONTH.value)
            viewModelKey = "$startTime - $endTime"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*val s = "${toLocalDate(startTime!!)} - ${toLocalDate(endTime!!)}"
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT)
            .show()*/
        return inflater.inflate(R.layout.fragment_report_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val s = "${toLocalDate(startTime!!)} - ${toLocalDate(endTime!!)}"
        net_income_txt.text=s
    }

    companion object {
        @JvmStatic
        fun newInstance(startTime: Long, endTime: Long, walletId: Long, timeRange: TimeRange) =
            ReportRecordFragment().apply {
                arguments = Bundle().apply {
                    putLong(START_TIME_PARAM, startTime)
                    putLong(END_TIME_PARAM, endTime)
                    putLong(WALLET_ID_PARAM, walletId)
                    putString(TIME_RANGE_PARAM, timeRange.value)
                }
            }
    }
}