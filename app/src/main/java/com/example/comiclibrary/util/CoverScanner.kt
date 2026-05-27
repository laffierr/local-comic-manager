package com.example.comiclibrary.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.comiclibrary.domain.model.CoverScanResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageEnumerator: ImageEnumerator
) {
    fun scanFolder(folderUri: Uri): CoverScanResult {
        val imageFiles = imageEnumerator.listImageFiles(folderUri)
        if (imageFiles.isEmpty()) return CoverScanResult(0, null)

        val coverDoc = findCoverFile(imageFiles)
        return CoverScanResult(
            pageCount = imageFiles.size,
            coverUri = coverDoc?.uri?.toString()
        )
    }

    fun resolveFolderName(folderUri: Uri): String {
        val doc = DocumentFile.fromTreeUri(context, folderUri)
        return doc?.name ?: "未知文件夹"
    }

    private fun findCoverFile(files: List<DocumentFile>): DocumentFile? {
        files.firstOrNull { doc ->
            val name = doc.name!!.lowercase()
            val nameWithoutExt = name.substringBeforeLast(".")
            Constants.COVER_NAMES.any { cover ->
                nameWithoutExt == cover || name.startsWith(cover)
            }
        }?.let { return it }

        return files.firstOrNull()
    }
}
