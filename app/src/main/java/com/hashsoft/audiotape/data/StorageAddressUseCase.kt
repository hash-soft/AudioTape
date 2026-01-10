package com.hashsoft.audiotape.data

import android.content.Context
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.StorageHelper
import jakarta.inject.Inject
import java.io.File

class StorageAddressUseCase @Inject constructor(
    private val _context: Context
) {

    fun pathToStorageLocationList(
        path: String,
        volumes: List<VolumeItem>
    ): List<StorageLocationDto> {
        // pathが空の場合ルート
        if (path.isEmpty()) {
            return listOf(
                StorageLocationDto(
                    _context.getString(R.string.storage_root),
                    "",
                    LocationType.Root
                )
            )
        }
        val storageIndex = volumes.indexOfFirst { path.startsWith(it.path) }
        // 存在しないpathを含んでいる場合ホームに変更し失敗したらルートで決定する
        val correctPath = if (storageIndex < 0) {
            StorageHelper.getHomePath()
        } else {
            path
        }
        if (correctPath.isEmpty()) {
            return listOf(
                StorageLocationDto(
                    _context.getString(R.string.storage_root),
                    "",
                    LocationType.Root
                )
            )
        }

        val storageData = volumes[storageIndex]
        var lastPath = storageData.path
        // ストレージの最初のパスを除去して階層をlist化する
        val directoryList = correctPath.removePrefix(lastPath).split(File.separator)
        return listOf(
            StorageLocationDto(_context.getString(R.string.storage_root), "", LocationType.Root),
            StorageLocationDto(
                storageData.name,
                storageData.path,
                if (storageData.isRemovable) LocationType.External else LocationType.Inner
            )
        ) + directoryList.mapNotNull {
            if (it.isEmpty()) return@mapNotNull null
            else {
                lastPath += File.separator + it
                StorageLocationDto(it, lastPath)
            }
        }
    }
}