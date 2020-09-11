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

    var categories: List<Category> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val root = inflater.inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(root)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}

class CategoryViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    private val categoryImg = root.findViewById<ImageView>(R.id.category_img)
    private val categoryText = root.findViewById<TextView>(R.id.category_text)

    fun bind(category: Category) {
        categoryImg.setImageResource(category.imageId)
        categoryText.text = category.name
        if (category.parentId != category.id) {
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(16, 0, 0, 0)
            root.layoutParams = params
        }
    }
}