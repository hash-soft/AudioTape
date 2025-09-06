package com.hashsoft.audiotape.data

import android.content.Context
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.StorageHelper
import java.io.File

class StorageAddressRepository(private val _context: Context) {
    fun pathToStorageLocationList(path: String): List<StorageLocationDto> {
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
        val volumes = StorageHelper.getVolumeList(_context)
        val storageIndex = volumes.indexOfFirst { path.startsWith(it.path) }
        // 存在しないpathを含んでいる場合ホームを試し失敗したらルートに変更する
        if (storageIndex < 0) {
            val homePath = StorageHelper.getHomePath()
            return if (homePath.isEmpty()) {
                listOf(
                    StorageLocationDto(
                        _context.getString(R.string.storage_root),
                        "",
                        LocationType.Root
                    )
                )
            } else {
                listOf(
                    StorageLocationDto(
                        _context.getString(R.string.storage_root),
                        "",
                        LocationType.Root
                    ),
                    StorageLocationDto(
                        _context.getString(R.string.storage_home),
                        homePath,
                        LocationType.Home
                    )
                )
            }
        }

        val storageData = volumes[storageIndex]
        var lastPath = storageData.path
        // ストレージの最初のパスを除去して階層をlist化する
        val directoryList = path.removePrefix(lastPath).split(File.separator)
        return listOf(
            StorageLocationDto(_context.getString(R.string.storage_root), "", LocationType.Root),
            StorageLocationDto(
                storageData.name,
                storageData.path,
                if (StorageHelper.getHomePath() == lastPath) LocationType.Home else LocationType.Normal
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