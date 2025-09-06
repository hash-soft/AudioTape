package com.hashsoft.audiotape.data

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
                // このフォルダはdbにまだ演奏していない
                Timber.i("findByPath is null: $path")
                AudioTapeDto("", "", -1)
            } else {
                Timber.d("audio tape: $it")
                AudioTapeDto(
                    it.folderPath,
                    it.currentName,
                    it.position,
                    AudioTapeSortOrder.fromInt(it.sortOrder),
                    it.speed
                )
            }
        }
    }

    suspend fun insertAll(dto: AudioTapeDto) =
        audioTapeDao.insertAll(
            AudioTapeEntity(
                dto.folderPath, dto.currentName, dto.position, dto.sortOrder.ordinal,
                dto.speed
            )
        )

    suspend fun upsertAll(dto: AudioTapeDto) =
        audioTapeDao.upsertAll(
            AudioTapeEntity(
                dto.folderPath,
                dto.currentName,
                dto.position,
                dto.sortOrder.ordinal,
                dto.speed
            )
        )

    suspend fun updatePosition(path: String, position: Long) = audioTapeDao.updatePosition(
        AudioTapePosition(path, position)
    )

    suspend fun updateCurrentNamePosition(path: String, currentName: String, position: Long) =
        audioTapeDao.updateCurrentNamePosition(
            AudioTapeCurrentNamePosition(path, currentName, position)
        )

    fun validAudioTapeDto(dto: AudioTapeDto): Boolean {
        return dto.folderPath.isNotEmpty() && dto.currentName.isNotEmpty() && dto.position >= 0
    }

}
