package com.example.inventory.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.example.inventory.data.Item
import com.example.inventory.data.getTmpFileName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

class EncFiles {
    private var masterKey: MasterKey? = null

    private fun getMasterKey(ctx: Context): MasterKey {
        if (masterKey == null) {
            masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }

        return masterKey!!
    }

    fun encryptData(
        ctx: Context,
        uri: Uri,
        item: Item,
    ) {

        val name = item.getTmpFileName()
        val tmp = File(ctx.cacheDir, name)

        if (tmp.exists()) {
            tmp.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            ctx,
            tmp,
            getMasterKey(ctx),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()

        var bytes = Json.encodeToString(item).toByteArray(StandardCharsets.UTF_8)

        Log.i("step 1", bytes.decodeToString())

        encryptedFile.openFileOutput().apply {
            write(bytes)
            flush()
            close()
        }

        bytes = tmp.readBytes()

        ctx.contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { o ->
                o.write(bytes)
                o.flush()
            }
        }

        Log.i("step 2", bytes.decodeToString())

        tmp.delete()
    }


    fun decryptDate(ctx: Context, uri: Uri): Item {
        val tmp = File(
            ctx.cacheDir,
            uri.getDisplayName(ctx.contentResolver),
        )

        ctx.contentResolver.openInputStream(uri)?.use { i ->
            ctx.contentResolver.openOutputStream(tmp.toUri())?.use { o ->
                i.copyTo(o)
            }
        }

        val encryptedFile = EncryptedFile.Builder(
            ctx,
            tmp,
            getMasterKey(ctx),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val bytesStr = encryptedFile.openFileInput().readBytes().decodeToString()

        Log.i("step 3", bytesStr)

        tmp.delete()

        return Json.decodeFromString(bytesStr)
    }
}