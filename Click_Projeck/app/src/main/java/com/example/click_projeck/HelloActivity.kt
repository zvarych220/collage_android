package com.example.click_projeck

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.click_projeck.databinding.ActivityHelloBinding

class HelloActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityHelloBinding
    var inputTextName: String = ""
    var flag = false

    companion object {
        const val REQUEST_CODE_MAIN = 1001
        const val REQUEST_CODE_RIP = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadData()
        super.onCreate(savedInstanceState)
        bindingClass = ActivityHelloBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingClass.root)

        updateUI()
    }

    private fun updateUI() {
        if (flag) {
            bindingClass.tvInHello.visibility = View.GONE
            bindingClass.tvHello.text = getString(R.string.hello_text_name, inputTextName)
            bindingClass.tbHello.text = getString(R.string.hello_bt)
        } else {
            bindingClass.tvInHello.visibility = View.VISIBLE
            bindingClass.tvInHello.setText(SpannableStringBuilder(""))  
            bindingClass.tvHello.text = getString(R.string.hello_text)
            bindingClass.tbHello.text = getString(R.string.bt_done)
        }
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    fun onClickSaveName(view: View) {
        if (!flag) {
            flag = true
            inputTextName = bindingClass.tvInHello.text.toString()
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Name", inputTextName)
        startActivityForResult(intent, REQUEST_CODE_MAIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_MAIN -> {
                if (resultCode == RIPActivity.RESULT_DELETE_PROFILE) {
                    flag = false
                    inputTextName = ""
                    val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()
                    updateUI()
                } else {
                    updateUI()
                }
            }
        }
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