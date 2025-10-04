package com.hashsoft.audiotape.data

import com.hashsoft.audiotape.logic.SystemTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class AudioTapeRepository(private val audioTapeDao: AudioTapeDao) {

    fun getAll(): Flow<List<AudioTapeDto>> {
        return audioTapeDao.getAll().map { list ->
            list.map {
                AudioTapeDto(
                    it.folderPath,
                    it.currentName,
                    it.position
                )
            }
        }
    }

    fun findByPath(path: String): Flow<AudioTapeDto> {
        return audioTapeDao.findByPath(path).map {
            if (it == null) {
                // このフォルダはdbにまだ存在していない
                Timber.i("findByPath is null: $path")
                AudioTapeDto("", "", -1)
            } else {
                Timber.d("audio tape: $it")
                AudioTapeDto(
                    folderPath = it.folderPath,
                    currentName = it.currentName,
                    position = it.position,
                    sortOrder = AudioTapeSortOrder.fromInt(it.sortOrder),
                    speed = it.speed
                )
            }
        }
    }

    suspend fun insertNew(
        folderPath: String,
        currentName: String,
        position: Long,
        sortOrder: AudioTapeSortOrder
    ): Long {
        val time = SystemTime.currentMillis()
        return audioTapeDao.insertAll(
            AudioTapeEntity(
                folderPath = folderPath,
                currentName = currentName,
                position = position,
                sortOrder = sortOrder.ordinal,
                createTime = time,
                updateTime = time
            )
        )
    }

    suspend fun updatePlayingPosition(folderPath: String, currentName: String, position: Long) =
        audioTapeDao.updatePlayingPosition(
            AudioTapePlayingPosition(
                folderPath = folderPath,
                currentName = currentName,
                position = position,
                updateTime = SystemTime.currentMillis()
            )
        )

    fun validAudioTapeDto(dto: AudioTapeDto): Boolean {
        return dto.folderPath.isNotEmpty() && dto.currentName.isNotEmpty() && dto.position >= 0
    }

}
