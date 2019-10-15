package uk.themeadow.doineedacache.util

import android.app.Activity
import android.widget.Toast
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import uk.themeadow.doineedacache.util.FileChooser.Companion.FILE_SELECT_CODE


class FileChooserImpl(val activity: Activity): FileChooser {
    override fun getFilename(start: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = start
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        Log.d("MAFCI", "Launch filepicker")
        if (intent.resolveActivity(activity.packageManager)!=null) {
            startActivityForResult(
                activity,
                Intent.createChooser(intent, "Select a File to Upload"),
                FILE_SELECT_CODE,
                null
            )
        } else {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(
                activity, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}

interface FileChooser {
    fun getFilename(start: String)

    companion object {
        val FILE_SELECT_CODE = 1234
    }
}

