package com.example.click_projeck

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.click_projeck.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    var index = 0
    var coin = 0
    var priseClick = 2
    var priseSteps = 2
    var inStep = 1
    var indexNumber = 0
    var step = 10
    var nickname = " "
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("Name")?.let {
            nickname = it
        }
        loadData()
        updateUI()
        setupClickListeners()
    }

    private fun updateUI() {
        binding.apply {
            tvPriseClick.text = getString(R.string.prise, priseClick)
            tvPriseStep.text = getString(R.string.prise, priseSteps)
            tvName.text = nickname

            // Update coin display
            tvCoin.visibility = View.VISIBLE
            tvCoin.text = getString(R.string.coin_name, coin)

            // Update step counter
            tvCount.visibility = View.VISIBLE
            tvCount.text = getString(R.string.count, inStep)

            // Update step display
            tvStep.visibility = View.VISIBLE
            tvStep.text = getString(R.string.step, step)

            // Update main counter
            tvText.text = index.toString()

            updateButtonText()

            // Debug log
            Log.d("MainFragment", "UI Updated - Coins: $coin, InStep: $inStep, Step: $step")
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            tvPriseClick.setOnClickListener {
                Log.d("MainFragment", "PriseClick clicked - Current coins: $coin, Price: $priseClick")
                onClickPlas()
            }



            tvPriseStep.setOnClickListener {
                Log.d("MainFragment", "PriseStep clicked - Current coins: $coin, Price: $priseSteps")
                onClickReduceStep()
            }

            btnClick.setOnClickListener {
                onClickBtn()
            }
        }
    }

    private fun onClickPlas() {
        if (coin >= priseClick) {
            if (inStep <= 9) {
                coin -= priseClick
                inStep++
                priseClick *= 2

                binding.apply {
                    tvCount.visibility = View.VISIBLE
                    tvCount.text = getString(R.string.count, inStep)
                    tvCoin.text = getString(R.string.coin_name, coin)

                    if (inStep == 9) {
                        tvPriseClick.text = getString(R.string.full_value)
                    } else {
                        tvPriseClick.text = getString(R.string.prise, priseClick)
                    }
                }

                saveDataAsync()
                Log.d("MainFragment", "Upgrade successful - New inStep: $inStep, Remaining coins: $coin")
            }
        } else {
            Toast.makeText(context, "Not enough coins! Need $priseClick coins.", Toast.LENGTH_SHORT).show()
            Log.d("MainFragment", "Not enough coins for upgrade - Has: $coin, Needs: $priseClick")
        }
    }

    private fun onClickReduceStep() {
        if (coin >= priseSteps) {
            if (step > 1) {
                coin -= priseSteps
                priseSteps *= 2
                step--

                binding.apply {
                    tvStep.visibility = View.VISIBLE
                    tvStep.text = getString(R.string.step, step)
                    tvCoin.text = getString(R.string.coin_name, coin)

                    if (step == 1) {
                        tvPriseStep.text = getString(R.string.full_value)
                    } else {
                        tvPriseStep.text = getString(R.string.prise, priseSteps)
                    }
                }

                saveDataAsync()
                Log.d("MainFragment", "Step reduced - New step: $step, Remaining coins: $coin")
            }
        } else {
            Toast.makeText(context, "Not enough coins! Need $priseSteps coins.", Toast.LENGTH_SHORT).show()
            Log.d("MainFragment", "Not enough coins for step reduction - Has: $coin, Needs: $priseSteps")
        }
    }

    private fun onClickBtn() {
        index += inStep
        indexNumber += inStep

        while (indexNumber >= step) {
            coin++
            indexNumber -= step
        }

        binding.apply {
            tvCoin.visibility = View.VISIBLE
            tvCoin.text = getString(R.string.coin_name, coin)
            tvText.text = index.toString()
        }

        updateButtonText()
        saveDataAsync()
        Log.d("MainFragment", "Button clicked - Index: $index, Coins: $coin")
    }

    private fun updateButtonText() {
        val text = when (index) {
            in 1..9 -> R.string.btn_name
            in 10..24 -> R.string.click_10
            in 25..49 -> R.string.click_25
            in 50..99 -> R.string.click_50
            in 100..199 -> R.string.click_100
            in 200..499 -> R.string.click_200
            in 500..799 -> R.string.click_500
            in 800..999 -> R.string.click_800
            in 1000..1999 -> R.string.click_1000
            in 2000..2999 -> R.string.click_2000
            in 3000..4999 -> R.string.click_3000
            in 5000..6999 -> R.string.click_5000
            in 7000..8999 -> R.string.click_7000
            in 9000..9999 -> R.string.click_9000
            else -> R.string.click_10000
        }
        binding.btnClick.setText(text)
    }

    private fun saveDataAsync() {
        lifecycleScope.launch(Dispatchers.IO) {
            saveData()
        }
    }

    private fun saveData() {
        try {
            requireContext().getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .edit()
                .putInt("index", index)
                .putInt("coin", coin)
                .putInt("priseClick", priseClick)
                .putInt("priseSteps", priseSteps)
                .putInt("inStep", inStep)
                .putInt("indexNumber", indexNumber)
                .putInt("step", step)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData() {
        try {
            requireContext().getSharedPreferences("game_data", Context.MODE_PRIVATE).let { prefs ->
                index = prefs.getInt("index", 0)
                coin = prefs.getInt("coin", 0)
                priseClick = prefs.getInt("priseClick", 2)
                priseSteps = prefs.getInt("priseSteps", 2)
                inStep = prefs.getInt("inStep", 1)
                indexNumber = prefs.getInt("indexNumber", 0)
                step = prefs.getInt("step", 10)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resetToDefaultValues()
        }
    }

    private fun resetToDefaultValues() {
        index = 0
        coin = 0
        priseClick = 2
        priseSteps = 2
        inStep = 1
        indexNumber = 0
        step = 10
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveData()
        _binding = null
    }

    companion object {
        fun newInstance(nickname: String) = MainFragment().apply {
            arguments = Bundle().apply {
                putString("Name", nickname)
            }
        }
    }
}