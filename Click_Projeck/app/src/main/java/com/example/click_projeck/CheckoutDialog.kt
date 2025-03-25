package com.example.click_projeck

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.click_projeck.databinding.DialogCheckoutBinding
import com.google.android.material.textfield.TextInputEditText

class CheckoutDialog(
    private val context: Context,
    private val totalAmount: Double,
    private val onOrderConfirmed: (
        recipientName: String,
        phoneNumber: String,
        deliveryAddress: String,
        paymentMethod: String,
        cardDetails: CardDetails?
    ) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogCheckoutBinding

    data class CardDetails(
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        // Додаємо TextWatcher для полів
        binding.etPhoneNumber.addTextChangedListener(PhoneNumberTextWatcher(binding.etPhoneNumber))
        binding.etCardNumber.addTextChangedListener(CardNumberTextWatcher(binding.etCardNumber))
        binding.etCardExpiry.addTextChangedListener(ExpiryDateTextWatcher(binding.etCardExpiry))
        binding.etCardCvv.addTextChangedListener(CvvTextWatcher(binding.etCardCvv))

        binding.rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            binding.llCardDetails.visibility = when (checkedId) {
                R.id.rbCardOnline -> View.VISIBLE
                else -> View.GONE
            }
        }

        binding.btnConfirmOrder.setOnClickListener {
            val recipientName = binding.etRecipientName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().replace(Regex("[^0-9]"), "")
            val deliveryAddress = binding.etDeliveryAddress.text.toString().trim()
            val paymentMethod = when (binding.rgPaymentMethod.checkedRadioButtonId) {
                R.id.rbCashOnDelivery -> "Наложений платіж"
                R.id.rbCardOnline -> "Оплата картою"
                else -> ""
            }

            // Валідація полів
            if (recipientName.isEmpty()) {
                binding.etRecipientName.error = "Введіть ім'я отримувача"
                return@setOnClickListener
            }

            if (phoneNumber.length != 12 || !phoneNumber.startsWith("380")) {
                binding.etPhoneNumber.error = "Введіть коректний номер у форматі 380XXXXXXXX"
                return@setOnClickListener
            }

            if (deliveryAddress.isEmpty()) {
                binding.etDeliveryAddress.error = "Введіть адресу доставки"
                return@setOnClickListener
            }

            val cardDetails = if (binding.rgPaymentMethod.checkedRadioButtonId == R.id.rbCardOnline) {
                val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
                val expiryDate = binding.etCardExpiry.text.toString()
                val cvv = binding.etCardCvv.text.toString()

                if (cardNumber.length != 16) {
                    binding.etCardNumber.error = "Введіть 16 цифр номера карти"
                    return@setOnClickListener
                }

                if (!expiryDate.matches(Regex("\\d{2}/\\d{2}"))) {
                    binding.etCardExpiry.error = "Введіть термін дії у форматі MM/YY"
                    return@setOnClickListener
                }

                if (cvv.length != 3) {
                    binding.etCardCvv.error = "Введіть 3 цифри CVV коду"
                    return@setOnClickListener
                }

                CardDetails(cardNumber, expiryDate, cvv)
            } else {
                null
            }

            onOrderConfirmed(recipientName, phoneNumber, deliveryAddress, paymentMethod, cardDetails)
            dismiss()
        }
    }

    // TextWatcher для номера телефону (формат 380XXXXXXXX)
    private inner class PhoneNumberTextWatcher(private val editText: TextInputEditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace(Regex("[^0-9]"), "")
            val formatted = when {
                cleanString.isEmpty() -> ""
                cleanString.startsWith("380") -> "380 ${cleanString.drop(3).chunked(2).joinToString(" ")}"
                else -> "380 ${cleanString.take(9).chunked(2).joinToString(" ")}"
            }

            editText.setText(formatted)
            editText.setSelection(formatted.length)
            editText.addTextChangedListener(this)
        }
    }

    // TextWatcher для номера карти (формат XXXX XXXX XXXX XXXX)
    private inner class CardNumberTextWatcher(private val editText: TextInputEditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace(Regex("[^0-9]"), "")
            val formatted = cleanString.chunked(4).joinToString(" ").take(19)

            editText.setText(formatted)
            editText.setSelection(formatted.length)
            editText.addTextChangedListener(this)
        }
    }

    // TextWatcher для терміну дії карти (формат MM/YY)
    private inner class ExpiryDateTextWatcher(private val editText: TextInputEditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace(Regex("[^0-9]"), "")
            val formatted = when {
                cleanString.isEmpty() -> ""
                cleanString.length <= 2 -> cleanString
                else -> "${cleanString.take(2)}/${cleanString.drop(2).take(2)}"
            }

            editText.setText(formatted)
            editText.setSelection(formatted.length)
            editText.addTextChangedListener(this)
        }
    }

    // TextWatcher для CVV коду (максимум 3 цифри)
    private inner class CvvTextWatcher(private val editText: TextInputEditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace(Regex("[^0-9]"), "")
            val formatted = cleanString.take(3)

            if (s.toString() != formatted) {
                editText.setText(formatted)
                editText.setSelection(formatted.length)
            }

            editText.addTextChangedListener(this)
        }
    }
}