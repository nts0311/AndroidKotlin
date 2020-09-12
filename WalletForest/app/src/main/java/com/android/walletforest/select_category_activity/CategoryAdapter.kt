package com.android.walletforest.select_category_activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.walletforest.R
import com.android.walletforest.model.Entities.Category

class CategoryAdapter() : RecyclerView.Adapter<CategoryViewHolder>() {

    var categoryClickListener: (category: Category) -> Unit = {}

    var categories: List<Category> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val root = inflater.inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(root, categoryClickListener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}

class CategoryViewHolder(
    private val root: View,
    private val categoryClickListener: (category: Category) -> Unit
) : RecyclerView.ViewHolder(root) {
    private val categoryImg = root.findViewById<ImageView>(R.id.category_img)
    private val categoryText = root.findViewById<TextView>(R.id.category_text)

    fun bind(category: Category) {
        root.setOnClickListener {
            categoryClickListener(category)
        }

        categoryImg.setImageResource(category.imageId)
        categoryText.text = category.name

        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        if (category.parentId != category.id)
            params.setMargins(48, 0, 0, 0)
        else
            params.setMargins(0,0,0,0)

        root.layoutParams = params
    }
}