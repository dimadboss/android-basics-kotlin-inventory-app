package com.example.inventory.utils

import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.inventory.data.Item
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter

class EncFiles {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun encryptData(
        ctx: Context,
        uri: Uri,
        item: Item,
    ) {
        val name = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val tmp = File(ctx.cacheDir, name)

        val encryptedFile = EncryptedFile.Builder(
            tmp,
            ctx,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()

        encryptedFile.openFileOutput().apply {
            write(Json.encodeToString(item).toByteArray())
            flush()
            close()
        }

        ctx.contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use {
                it.write(tmp.readBytes())
            }
        }
    }
}