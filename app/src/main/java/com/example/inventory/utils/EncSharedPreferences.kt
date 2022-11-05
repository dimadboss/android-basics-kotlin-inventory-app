package com.example.inventory.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.inventory.data.*


class EncSharedPreferences {
    private lateinit var sharedPreferences: SharedPreferences

    private val preferencesName = "SharedPreferences"

    private var preferencesCache: Preferences? = null

    fun initEncryptedSharedPreferences(ctx: Context) {
        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        sharedPreferences = EncryptedSharedPreferences.create(
            preferencesName,
            masterKeyAlias,
            ctx,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun isInit(): Boolean {
        return ::sharedPreferences.isInitialized
    }

    fun setPreferences(p: Preferences) {
        if (!isInit()) {
            throw Exception("preference storage was not initialized")
        }

        setStringPreference(defaultProviderEmailKey, p.defaultProviderEmail)
        setStringPreference(defaultProviderPhoneKey, p.defaultProviderPhone)
        setStringPreference(defaultProviderNameKey, p.defaultProviderName)

        setBoolPreference(hideSensitiveDataKey, p.hideSensitiveData)
        setBoolPreference(useDefaultValuesKey, p.useDefaultValues)
        setBoolPreference(preventSharingKey, p.preventSharing)

        preferencesCache = p
    }

    fun getPreferences(): Preferences {
        if (!isInit()) {
            throw Exception("preference storage was not initialized")
        }

        if (preferencesCache != null) {
            return preferencesCache!!
        }

        preferencesCache = Preferences(
            getStringPreference(defaultProviderEmailKey),
            getStringPreference(defaultProviderPhoneKey),
            getStringPreference(defaultProviderNameKey),

            getBoolPreference(hideSensitiveDataKey),
            getBoolPreference(useDefaultValuesKey),
            getBoolPreference(preventSharingKey),
        )

        return preferencesCache!!
    }

    private fun getStringPreference(key: String): String {
        return sharedPreferences.getString(key, defaultString) ?: defaultString
    }

    private fun getBoolPreference(key: String): Boolean {
        return sharedPreferences.getBoolean(key, defaultBool)
    }

    private fun setStringPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun setBoolPreference(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
}