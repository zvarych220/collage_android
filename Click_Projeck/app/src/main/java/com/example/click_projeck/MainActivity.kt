package com.example.click_projeck

import android.os.Bundle
<<<<<<< Updated upstream
import android.view.View
=======
>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.click_projeck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
<<<<<<< Updated upstream
    var index = 0
    var coin = 0
    var priseClick = 2
    var priseSteps = 2
    var inStep = 1
    var indexNumber = 0
    var step = 10
    lateinit var bindingClass: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        loadData()
        bindingClass.tvPriseClick.text = getString(R.string.prise, priseClick)
        bindingClass.tvPriseStep.text = getString(R.string.prise, priseSteps)
    }
    override fun onStop() {
        super.onStop()
        saveData()
    }


    fun onClickPlas(view: View){
        if(coin >= priseClick){
           if (inStep<=9){
               inStep++
               coin -= priseClick
               priseClick *= 2
               bindingClass.tvPriseClick.text = getString(R.string.prise, priseClick)
               bindingClass.tvCount.visibility = View.VISIBLE

               bindingClass.tvCount.text = getString(R.string.count, inStep)
               bindingClass.tvPriseStep.text = getString(R.string.prise, priseSteps)
           }


            if (inStep == 9 ){
                bindingClass.tvPriseClick.text = getString(R.string.full_value)
=======
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide bottom navigation on auth screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> binding.bottomNavigation.visibility = android.view.View.GONE
                else -> binding.bottomNavigation.visibility = android.view.View.VISIBLE
>>>>>>> Stashed changes
            }
        }

<<<<<<< Updated upstream
    fun onClickReduceStep(view: View){
        if(coin >= priseSteps){
            if (step > 1) {
                coin -= priseSteps
                priseSteps *= 2
                step--
                bindingClass.tvPriseStep.text = getString(R.string.prise, priseSteps)
                bindingClass.tvStep.visibility = View.VISIBLE
                bindingClass.tvStep.text = getString(R.string.step, step)
            }
            if (step == 1 ){
                bindingClass.tvPriseStep.text = getString(R.string.full_value)
            }


=======
        // Get nickname from intent and pass it to MainFragment
        val nickname = intent.getStringExtra("Name") ?: ""
        val bundle = Bundle().apply {
            putString("Name", nickname)
>>>>>>> Stashed changes
        }
        navController.setGraph(R.navigation.nav_graph, bundle)
    }
<<<<<<< Updated upstream

    fun onClickBtn (view: View){
        index += inStep

        indexNumber+=inStep
        while (indexNumber >= step){
            coin++
            indexNumber -= step
        }
        if(coin >= 0 ){
            var textCoin = getString(R.string.coin_name, coin)
            bindingClass.tvCoin.text = textCoin
            bindingClass.tvCoin.visibility = View.VISIBLE
        }
        when (index){
            in 1..9 ->bindingClass.btnClick.text = getString(R.string.btn_name)
            in 10..24 -> bindingClass.btnClick.text = getString(R.string.click_10)
            in 25..49 -> bindingClass.btnClick.text = getString(R.string.click_25)
            in 50..99 -> bindingClass.btnClick.text = getString(R.string.click_50)
            in 100..199 -> bindingClass.btnClick.text = getString(R.string.click_100)
            in 200..499 -> bindingClass.btnClick.text = getString(R.string.click_200)
            in 500..799 -> bindingClass.btnClick.text = getString(R.string.click_500)
            in 800..999 -> bindingClass.btnClick.text = getString(R.string.click_800)
            in 1000..1999 -> bindingClass.btnClick.text = getString(R.string.click_1000)
            in 2000..2999 -> bindingClass.btnClick.text = getString(R.string.click_2000)
            in 3000..4999 -> bindingClass.btnClick.text = getString(R.string.click_3000)
            in 5000..6999 -> bindingClass.btnClick.text = getString(R.string.click_5000)
            in 7000..8999 -> bindingClass.btnClick.text = getString(R.string.click_7000)
            in 9000..9999 -> bindingClass.btnClick.text = getString(R.string.click_9000)
            in 10000..10100 -> bindingClass.btnClick.text = getString(R.string.click_10000)
            else->{
                val intent = Intent(this, RIPActivity::class.java)
                startActivity(intent);
            }
        }
        bindingClass.tvText.text = index.toString()

    }


    private fun saveData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("index", index)
        editor.putInt("coin", coin)
        editor.putInt("priseClick", priseClick)
        editor.putInt("priseSteps", priseSteps)
        editor.putInt("inStep", inStep)
        editor.putInt("indexNumber", indexNumber)
        editor.putInt("step", step)
        editor.apply()
    }


    private fun loadData() {
        val sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE)
        index = sharedPreferences.getInt("index", 0)
        coin = sharedPreferences.getInt("coin", 0)
        priseClick = sharedPreferences.getInt("priseClick", 2)
        priseSteps = sharedPreferences.getInt("priseSteps", 2)
        inStep = sharedPreferences.getInt("inStep", 1)
        indexNumber = sharedPreferences.getInt("indexNumber", 0)
        step = sharedPreferences.getInt("step", 10)
    }

}
=======
}
>>>>>>> Stashed changes
