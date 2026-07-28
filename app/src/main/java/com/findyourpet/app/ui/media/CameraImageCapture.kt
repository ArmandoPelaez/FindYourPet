package com.findyourpet.app.ui.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CameraImageCapture {
    fun createOutputUri(context: Context): Uri {
        val directory = File(context.cacheDir, "camera_images").apply { mkdirs() }
        val file = File.createTempFile("pet_", ".jpg", directory)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun cleanUp(context: Context, uri: Uri) {
        if (uri.scheme != "content") return
        val directory = File(context.cacheDir, "camera_images")
        directory.listFiles()
            ?.filter { it.length() == 0L || it.name == uri.lastPathSegment }
            ?.forEach { it.delete() }
    }
}
