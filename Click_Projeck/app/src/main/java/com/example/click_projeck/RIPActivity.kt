package com.example.click_projeck

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.click_projeck.databinding.ActivityRipactivityBinding

class RIPActivity : AppCompatActivity() {
    var inputTextName: String = ""
    var flagdalate = false
    lateinit var bindingClass: ActivityRipactivityBinding

    companion object {
        const val RESULT_DELETE_PROFILE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRipactivityBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        loadData()

        inputTextName = intent.getStringExtra("name") ?: ""
        bindingClass.textView.visibility = View.VISIBLE
        bindingClass.imageView.visibility = View.VISIBLE
        bindingClass.textView.text = inputTextName
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    fun onClickDalate(view: View) {
        flagdalate = true
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        setResult(RESULT_DELETE_PROFILE)
        finish()
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("flag", flagdalate)
        editor.putString("name", inputTextName)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        flagdalate = sharedPreferences.getBoolean("flag", false)
        inputTextName = sharedPreferences.getString("name", "") ?: ""
    }
}