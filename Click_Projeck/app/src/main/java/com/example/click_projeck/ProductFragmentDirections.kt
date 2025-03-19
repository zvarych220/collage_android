package com.example.click_projeck

import androidx.navigation.NavDirections
import androidx.navigation.NavController

class ProductFragmentDirections private constructor() {
    companion object {
        fun actionProductFragmentToProductDetailFragment(productId: Int): NavDirections {
            return object : NavDirections {
                override val actionId = R.id.actionProductFragmentToProductDetailFragment
                override val arguments = androidx.core.os.bundleOf("productId" to productId)
            }
        }
    }
}

class ProductListFragmentDirections private constructor() {
    companion object {
        fun actionProductListFragmentToProductDetailFragment(productId: Int): NavDirections {
            return object : NavDirections {
                override val actionId = R.id.actionProductListFragmentToProductDetailFragment
                override val arguments = androidx.core.os.bundleOf("productId" to productId)
            }
        }
    }
}