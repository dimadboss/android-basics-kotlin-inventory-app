package com.example.inventory.validator

import android.text.TextUtils
import com.example.inventory.R

class PhoneValidator(private val phone: String) : BaseValidator() {
    override fun validate(): ValidateResult {
        val isValid =
            !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone)
                .matches()

        return ValidateResult(
            isValid,
            if (isValid) R.string.text_validation_success else R.string.text_validation_error_phone
        )
    }
}