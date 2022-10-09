package com.example.inventory.validator

import com.example.inventory.R

abstract class BaseValidator: IValidator {
    companion object{
        fun validate(vararg validators: IValidator): ValidateResult{
            validators.forEach {
                val result = it.validate()
                if (!result.isSuccess) {
                    return result
                }
            }
            return ValidateResult(true, R.string.text_validation_success)
        }
    }
}