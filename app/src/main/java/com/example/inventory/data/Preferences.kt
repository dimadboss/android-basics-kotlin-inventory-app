package com.example.inventory.data

const val defaultString = ""
const val defaultBool = false


const val defaultProviderEmailKey = "defaultProviderEmailKey"
const val defaultProviderPhoneKey = "defaultProviderPhoneKey"
const val defaultProviderNameKey = "defaultProviderNameKey"

const val hideSensitiveDataKey = "hideSensitiveDataKey"
const val useDefaultValuesKey = "useDefaultValuesKey"
const val preventSharingKey = "preventSharingKey"

data class Preferences(
    val defaultProviderEmail: String,
    val defaultProviderPhone: String,
    val defaultProviderName: String,

    val hideSensitiveData: Boolean,
    val useDefaultValues: Boolean,
    val preventSharing: Boolean,
)