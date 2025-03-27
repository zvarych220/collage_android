package com.example.click_projeck

import android.os.Bundle
import androidx.navigation.NavArgs

class ProductDetailFragmentArgs private constructor(val productId: Int) : NavArgs {

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt("productId", this.productId)
        return bundle
    }

    companion object {
        @JvmStatic
        fun fromBundle(bundle: Bundle): ProductDetailFragmentArgs {
            bundle.classLoader = ProductDetailFragmentArgs::class.java.classLoader
            val productId = bundle.getInt("productId")
            return ProductDetailFragmentArgs(productId)
        }

        @JvmStatic
        fun fromSavedStateHandle(savedStateHandle: androidx.lifecycle.SavedStateHandle): ProductDetailFragmentArgs {
            val productId = savedStateHandle.get<Int>("productId") ?: throw IllegalArgumentException("Required argument \"productId\" is missing")
            return ProductDetailFragmentArgs(productId)
        }
    }
}