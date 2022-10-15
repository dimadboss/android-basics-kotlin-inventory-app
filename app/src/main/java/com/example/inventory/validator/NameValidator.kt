package com.example.inventory.validator

import android.text.TextUtils
import com.example.inventory.R

class NameValidator(private val name: String) : BaseValidator() {
    override fun validate(): ValidateResult {
        val isValid = !TextUtils.isEmpty(name) && name.matches("^[a-zA-Z ]*$".toRegex())

        return ValidateResult(
            isValid,
            if (isValid) R.string.text_validation_success else R.string.text_validation_error_name
        )
    }
}