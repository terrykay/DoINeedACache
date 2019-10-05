package com.example.doineedacache.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log

fun Uri.getPath(context: Context): String? {

    Log.d("UE", "Scheme = ${this.scheme}")

    if ("content" == this.getScheme()?.toLowerCase()) {
        val projection = arrayOf("_data")

        try {
            val cursor = context.getContentResolver().query(this, projection, null, null, null);
            val column_index = cursor?.getColumnIndexOrThrow("_data")
            if (cursor?.moveToFirst() == true) {
                Log.d("UE", "Value = ${cursor.getString(0)}")
                Log.d("UE", "Value = ${cursor.getBlob(0)}")
                return cursor.getString(column_index!!)
            }
        } catch (e: Exception) {
            Log.e("UE", "${e.printStackTrace()}")
            // Eat it
        }
    } else if ("file" == (this.getScheme()?.toLowerCase())) {
        return this.getPath() as String
    }

    return null
}