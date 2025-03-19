package com.example.click_projeck

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ProductImageAdapter(
    private val images: MutableList<ProductImage>,
    private val onDeleteClick: (Int) -> Unit,
    private val onMainImageClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_product_image)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteImage)
        val mainImageCheckbox: CheckBox = view.findViewById(R.id.cbMainImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val productImage = images[position]
        holder.imageView.setImageBitmap(productImage.bitmap)
        holder.mainImageCheckbox.isChecked = productImage.isMain

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }

        holder.mainImageCheckbox.setOnClickListener {
            if (holder.mainImageCheckbox.isChecked) {
                onMainImageClick(position)
            } else {
                // If the user unchecks the main image, we should re-check it
                // since we need to have a main image
                holder.mainImageCheckbox.isChecked = true
            }
        }
    }

    override fun getItemCount() = images.size
}

// Data class to represent a product image
data class ProductImage(
    val bitmap: Bitmap,
    val base64: String,
    var isMain: Boolean = false
)