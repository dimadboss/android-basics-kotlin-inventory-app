/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.Application
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.inventory.data.ItemRoomDatabase
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

const val DBKeyAlias = "RoomSQLiteDBKey"
const val KeyStoreProvider = "AndroidKeyStore"
const val KeyPurpose = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT

class InventoryApplication : Application() {
    val database: ItemRoomDatabase by lazy {
        val ks = KeyStore.getInstance(KeyStoreProvider).apply { load(null) }

        val secretKeyEntry = ks.getEntry(DBKeyAlias, null) as? KeyStore.SecretKeyEntry

        val secretKey: SecretKey

        if (secretKeyEntry == null) {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KeyStoreProvider)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(DBKeyAlias, KeyPurpose)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setUserAuthenticationRequired(true)
                    .build()
            )
            secretKey = keyGenerator.generateKey()
        } else {
            secretKey = secretKeyEntry.secretKey
        }

        ItemRoomDatabase.getDatabase(this, secretKey.toString().toByteArray())
    }
}
