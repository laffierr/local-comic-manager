package com.example.comiclibrary.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.LinkedHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageEnumerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = object : LinkedHashMap<String, List<String>>(32, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<String>>?): Boolean {
            return size > 50
        }
    }

    fun listImageFiles(folderUri: Uri): List<DocumentFile> {
        val root = DocumentFile.fromTreeUri(context, folderUri) ?: return emptyList()
        return root.listFiles()
            .filter { doc ->
                doc.isFile && doc.name != null &&
                    Constants.SUPPORTED_IMAGE_EXTENSIONS.any { ext ->
                        doc.name!!.lowercase().endsWith(ext)
                    }
            }
            .sortedBy { doc -> doc.name!!.lowercase() }
    }

    fun listImageUris(folderUri: Uri): List<String> {
        val key = folderUri.toString()
        cache[key]?.let { return it }
        val result = listImageFiles(folderUri).map { it.uri.toString() }
        cache[key] = result
        return result
    }

    fun getImageCount(folderUri: Uri): Int {
        val root = DocumentFile.fromTreeUri(context, folderUri) ?: return 0
        return root.listFiles().count { doc ->
            doc.isFile && doc.name != null &&
                Constants.SUPPORTED_IMAGE_EXTENSIONS.any { ext ->
                    doc.name!!.lowercase().endsWith(ext)
                }
        }
    }
}
