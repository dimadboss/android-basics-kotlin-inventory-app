package com.example.inventory.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 *
 * Extension class for Uri based classes
 *
 * @author Danijel Sokač
 */

/**
 * Returns display file name of [this] using [contentResolver]
 *
 * @param contentResolver [ContentResolver] used to access file info
 */
fun Uri.getDisplayName(contentResolver: ContentResolver): String {
    val cursor = contentResolver
        .query(this, null, null, null, null, null)

    cursor.use { c ->
        // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
        // "if there's anything to look at, look at it" conditionals.
        if (c != null && c.moveToFirst()) {

            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx == -1) {
                return ""
            }

            // Note it's called "Display Name".  This is
            // provider-specific, and might not necessarily be the file name.
            return c.getString(idx)
        }
    }

    return ""
}