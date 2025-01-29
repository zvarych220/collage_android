package com.example.click_projeck

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.click_projeck.databinding.ActivityRipactivityBinding


class RIPActivity : AppCompatActivity() {
    var inputTextName: String = ""
    var flag = false
    lateinit var bindingClass: ActivityRipactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRipactivityBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        loadData()
        if (flag == true){
            inputTextName =  bindingClass.inputName.text.toString()
            bindingClass.button.visibility = View.GONE
            bindingClass.tvView.visibility = View.GONE
            bindingClass.textView.visibility = View.VISIBLE
            bindingClass.imageView.visibility = View.VISIBLE
            bindingClass.textView.text = inputTextName
            bindingClass.inputName.visibility = View.GONE
        }

    }
    override fun onStop() {
        super.onStop()
        saveData()
    }
    override fun onPause() {
        super.onPause()
        saveData()
    }



    fun onClickDone(view: View){
        flag = true
        inputTextName = bindingClass.inputName.text.toString()
        bindingClass.button.visibility = View.GONE
        bindingClass.tvView.visibility = View.GONE
        bindingClass.textView.visibility = View.VISIBLE
        bindingClass.imageView.visibility = View.VISIBLE
        bindingClass.textView.text = inputTextName
        bindingClass.inputName.visibility = View.GONE
    }


    private fun saveData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("flag", flag)
        editor.putString("name", inputTextName)
        editor.apply()
    }


    private fun loadData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        flag = sharedPreferences.getBoolean("flag", false)
        inputTextName = sharedPreferences.getString("name", "") ?: ""
    }
}