package com.android.walletforest.select_image_activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R

class IconAdapter : RecyclerView.Adapter<IconViewHolder>() {

    private val imageId = listOf(
        R.drawable.icon_1,
        R.drawable.icon_2,
        R.drawable.icon_3,
        R.drawable.icon_4,
        R.drawable.icon_5,
        R.drawable.icon_6,
        R.drawable.icon_7,
        R.drawable.icon_8,
        R.drawable.icon_9,
        R.drawable.icon_10,
        R.drawable.icon_11,
        R.drawable.icon_12,
        R.drawable.icon_13,
        R.drawable.icon_14,
        R.drawable.icon_15,
        R.drawable.icon_16,
        R.drawable.icon_17,
        R.drawable.icon_18,
        R.drawable.icon_19,
        R.drawable.icon_20,
        R.drawable.icon_21,
        R.drawable.icon_22,
        R.drawable.icon_23,
        R.drawable.icon_24,
        R.drawable.icon_25,
        R.drawable.icon_26,
        R.drawable.icon_27,
        R.drawable.icon_28,
        R.drawable.icon_29,
        R.drawable.icon_30,
        R.drawable.icon_31,
        R.drawable.icon_32,
        R.drawable.icon_33,
        R.drawable.icon_34,
        R.drawable.icon_35,
        R.drawable.icon_36,
        R.drawable.icon_37,
        R.drawable.icon_38,
        R.drawable.icon_39,
        R.drawable.icon_40,
        R.drawable.icon_41,
        R.drawable.icon_42,
        R.drawable.icon_43,
        R.drawable.icon_44,
        R.drawable.icon_45,
        R.drawable.icon_46,
        R.drawable.icon_47,
        R.drawable.icon_48,
        R.drawable.icon_49,
        R.drawable.icon_50,
        R.drawable.icon_51,
        R.drawable.icon_52,
        R.drawable.icon_53,
        R.drawable.icon_54,
        R.drawable.icon_55,
        R.drawable.icon_56,
        R.drawable.icon_57,
        R.drawable.icon_58,
        R.drawable.icon_59,
        R.drawable.icon_60,
        R.drawable.icon_61,
        R.drawable.icon_62,
        R.drawable.icon_63,
        R.drawable.icon_64,
        R.drawable.icon_65,
        R.drawable.icon_66,
        R.drawable.icon_67,
        R.drawable.icon_68,
        R.drawable.icon_69,
        R.drawable.icon_70,
        R.drawable.icon_71,
        R.drawable.icon_72,
        R.drawable.icon_73,
        R.drawable.icon_74,
        R.drawable.icon_75,
        R.drawable.icon_76,
        R.drawable.icon_77,
        R.drawable.icon_78,
        R.drawable.icon_79,
        R.drawable.icon_80,
        R.drawable.icon_81,
        R.drawable.icon_82,
        R.drawable.icon_83,
        R.drawable.icon_84,
        R.drawable.icon_85,
        R.drawable.icon_86,
        R.drawable.icon_87,
        R.drawable.icon_88,
        R.drawable.icon_89,
        R.drawable.icon_90,
        R.drawable.icon_91,
        R.drawable.icon_92,
        R.drawable.icon_93,
        R.drawable.icon_94,
        R.drawable.icon_95,
        R.drawable.icon_96,
        R.drawable.icon_97,
        R.drawable.icon_98,
        R.drawable.icon_99,
        R.drawable.icon_100,
        R.drawable.icon_101,
        R.drawable.icon_102,
        R.drawable.icon_103,
        R.drawable.icon_104,
        R.drawable.icon_105,
        R.drawable.icon_106,
        R.drawable.icon_107,
        R.drawable.icon_108,
        R.drawable.icon_109,
        R.drawable.icon_110,
        R.drawable.icon_111,
        R.drawable.icon_112,
        R.drawable.icon_113,
        R.drawable.icon_114,
        R.drawable.icon_115,
        R.drawable.icon_116,
        R.drawable.icon_117,
        R.drawable.icon_118,
        R.drawable.icon_119,
        R.drawable.icon_120,
        R.drawable.icon_121,
        R.drawable.icon_122,
        R.drawable.icon_123,
        R.drawable.icon_124,
        R.drawable.icon_125,
        R.drawable.icon_126,
        R.drawable.icon_127,
        R.drawable.icon_128,
        R.drawable.icon_129,
        R.drawable.icon_130,
        R.drawable.icon_131,
        R.drawable.icon_132,
        R.drawable.icon_133,
        R.drawable.icon_134,
        R.drawable.icon_135,
        R.drawable.icon_136,
        R.drawable.icon_137,
        R.drawable.icon_138,
        R.drawable.icon_139,
        R.drawable.icon_140,
        R.drawable.icon_141,
        R.drawable.icon_142,
        R.drawable.icon_143,
        R.drawable.icon_144,
        R.drawable.ic_category_all,
        R.drawable.ic_category_award,
        R.drawable.ic_category_debt,
        R.drawable.ic_category_doctor,
        R.drawable.ic_category_donations,
        R.drawable.ic_category_education,
        R.drawable.ic_category_entertainment,
        R.drawable.ic_category_family,
        R.drawable.ic_category_foodndrink,
        R.drawable.ic_category_friendnlover,
        R.drawable.ic_category_give,
        R.drawable.ic_category_interestmoney,
        R.drawable.ic_category_invest,
        R.drawable.ic_category_loan,
        R.drawable.ic_category_medical,
        R.drawable.ic_category_other_cates,
        R.drawable.ic_category_other_chart,
        R.drawable.ic_category_other_expense,
        R.drawable.ic_category_other_income,
        R.drawable.ic_category_pharmacy,
        R.drawable.ic_category_salary,
        R.drawable.ic_category_selling,
        R.drawable.ic_category_shopping,
        R.drawable.ic_category_transport,
        R.drawable.ic_category_travel,
        R.drawable.ic_clipboard,
        R.drawable.ic_launcher_tools_bill,
        R.drawable.ic_launcher_tools_find_baatm,
        R.drawable.ic_launcher_tools_find_bank,
        R.drawable.ic_launcher_tools_interest_rate,
        R.drawable.ic_launcher_tools_tax_calculator,
        R.drawable.ic_launcher_tools_tip_calculator,
        R.drawable.ic_launcher_tools_webdisplay
    )

    var itemClickListener: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder =
        IconViewHolder.from(parent)

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(imageId[position], itemClickListener)
    }

    override fun getItemCount(): Int = imageId.size
}

class IconViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    private val iconImg = root.findViewById<ImageView>(R.id.item_icon)

    fun bind(imgId: Int, itemClickListener: (Int) -> Unit) {
        iconImg.setImageResource(imgId)
        root.setOnClickListener {
            itemClickListener.invoke(imgId)
        }
    }

    companion object {
        fun from(parent: ViewGroup): IconViewHolder {
            val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon, parent, false)

            return IconViewHolder(root)
        }
    }
}