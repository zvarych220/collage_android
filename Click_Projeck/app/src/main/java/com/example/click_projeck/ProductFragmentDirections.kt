package com.example.click_projeck

import androidx.navigation.NavDirections

class ProductFragmentDirections private constructor() {
    companion object {
        fun actionProductFragmentToProductDetailFragment(productId: Int): NavDirections {
            return object : NavDirections {
                override val actionId = R.id.action_productFragment_to_productDetailFragment // Note the underscore
                override val arguments = androidx.core.os.bundleOf("productId" to productId)
            }
        }
    }
}