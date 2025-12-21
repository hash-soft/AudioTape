package com.hashsoft.audiotape.data

import com.hashsoft.audiotape.logic.SystemTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import timber.log.Timber

/**
 * オーディオテープのデータ操作を行うリポジトリ
 *
 * @param audioTapeDao オーディオテープのDAO
 */
class AudioTapeRepository(private val audioTapeDao: AudioTapeDao) {

    val ramAudioTapeFlow = MutableSharedFlow<Map<String, AudioTapeDto?>>()
    val ramSortOrderFlow = MutableSharedFlow<Map<String, AudioTapeSortOrder?>>()

    /**
     * すべてのオーディオテープを取得する
     *
     * @return オーディオテープのリストをFlowで返す
     */
    fun getAll(sort: AudioTapeListSortOrder): Flow<List<AudioTapeDto>> {
        val audioTapeFlow = when (sort) {
            AudioTapeListSortOrder.NAME_ASC -> audioTapeDao.getAllByNameAsc()
            AudioTapeListSortOrder.NAME_DESC -> audioTapeDao.getAllByNameDesc()
            AudioTapeListSortOrder.LAST_PLAYED_ASC -> audioTapeDao.getAllByLastPlayedAsc()
            AudioTapeListSortOrder.LAST_PLAYED_DESC -> audioTapeDao.getAllByLastPlayedDesc()
            AudioTapeListSortOrder.CREATED_ASC -> audioTapeDao.getAllByCreatedAsc()
            AudioTapeListSortOrder.CREATED_DESC -> audioTapeDao.getAllByCreatedDesc()
        }
        return audioTapeFlow.map { list ->
            list.map {
                convertEntityToDto(it)
            }
        }
    }

    /**
     * パスを指定してオーディオテープを検索する
     *
     * @param path フォルダのパス
     * @return 見つかったオーディオテープ、見つからない場合は空のDTOをFlowで返す
     */
    fun findByPath(path: String): Flow<AudioTapeDto?> {
        return merge(ramAudioTapeFlow.map { it[path] }, audioTapeDao.findByPath(path).map {
            if (it == null) {
                // このフォルダはdbにまだ存在していない
                Timber.i("findByPath is null: $path")
                null
            } else {
                Timber.d("audio tape: $it")
                convertEntityToDto(it)
            }
        }).distinctUntilChanged()
    }

    suspend fun getByPath(path: String): AudioTapeDto? = findByPath(path).first()


    /**
     * パスを指定してソート順を検索する
     *
     * @param path フォルダのパス
     * @return 見つかったソート順、見つからない場合はデフォルト値をFlowで返す
     */
    fun findSortOrderByPath(path: String): Flow<AudioTapeSortOrder?> {
        return merge(ramSortOrderFlow.map { it[path] }, audioTapeDao.findSortOrderByPath(path).map {
            if (it == null) {
                Timber.i("findSortOrderByPath is null: $path")
                null
            } else {
                AudioTapeSortOrder.fromInt(it)
            }
        }).distinctUntilChanged()
    }

    /**
     * [AudioTapeEntity]を[AudioTapeDto]に変換する
     *
     * @param entity 変換元のエンティティ
     * @return 変換後のDTO
     */
    private fun convertEntityToDto(entity: AudioTapeEntity): AudioTapeDto {
        return AudioTapeDto(
            folderPath = entity.folderPath,
            currentName = entity.currentName,
            position = entity.position,
            tapeName = entity.tapeName,
            sortOrder = AudioTapeSortOrder.fromInt(entity.sortOrder),
            repeat = entity.repeat > 0,
            volume = entity.volume,
            speed = entity.speed,
            pitch = entity.pitch,
            itemCount = entity.itemCount,
            totalTime = entity.totalTime,
            lastPlayedAt = entity.lastPlayedAt,
            createTime = entity.createTime,
            updateTime = entity.updateTime
        )
    }


    suspend fun insertNew(audioTape: AudioTapeDto): Long {
        val time = SystemTime.currentMillis()
        return audioTapeDao.insertAll(
            AudioTapeEntity(
                folderPath = audioTape.folderPath,
                currentName = audioTape.currentName,
                position = audioTape.position,
                tapeName = audioTape.tapeName,
                sortOrder = audioTape.sortOrder.ordinal,
                repeat = if (audioTape.repeat) 2 else 0,
                volume = audioTape.volume,
                speed = audioTape.speed,
                pitch = audioTape.pitch,
                itemCount = audioTape.itemCount,
                totalTime = audioTape.totalTime,
                lastPlayedAt = time,
                createTime = time,
                updateTime = time
            )
        )
    }

    /**
     * 再生位置を更新する
     *
     * @param folderPath フォルダのパス
     * @param currentName 現在のアイテム名
     * @param position 再生位置
     */
    suspend fun updatePlayingPosition(
        folderPath: String,
        currentName: String,
        position: Long,
        isLastPlayedAt: Boolean
    ) {
        val time = SystemTime.currentMillis()
        if (isLastPlayedAt) {
            audioTapeDao.updatePlayingPositionWithLastPlayedAt(
                AudioTapePlayingPositionWithLastPlayedAt(
                    folderPath = folderPath,
                    currentName = currentName,
                    position = position,
                    lastPlayedAt = time,
                    updateTime = time,
                )
            )
        } else {
            audioTapeDao.updatePlayingPosition(
                AudioTapePlayingPosition(
                    folderPath = folderPath,
                    currentName = currentName,
                    position = position,
                    updateTime = time,
                )
            )
        }
    }

    /**
     * ソート順を更新する
     *
     * @param folderPath フォルダのパス
     * @param sortOrder ソート順
     */
    suspend fun updateSortOrder(folderPath: String, sortOrder: AudioTapeSortOrder) =
        audioTapeDao.updateSortOrder(
            AudioTapeSortOrderSubEntity(
                folderPath = folderPath,
                sortOrder = sortOrder.ordinal,
                updateTime = SystemTime.currentMillis()
            )
        )

    /**
     * リピート設定を更新する
     *
     * @param folderPath フォルダのパス
     * @param repeat リピート設定
     */
    suspend fun updateRepeat(folderPath: String, repeat: Boolean) = audioTapeDao.updateRepeat(
        AudioTapeRepeat(
            folderPath = folderPath,
            repeat = if (repeat) 2 else 0,
            updateTime = SystemTime.currentMillis()
        )
    )

    /**
     * 音量を更新する
     *
     * @param folderPath フォルダのパス
     * @param volume 音量
     */
    suspend fun updateVolume(folderPath: String, volume: Float) = audioTapeDao.updateVolume(
        AudioTapeVolume(
            folderPath = folderPath,
            volume = volume,
            updateTime = SystemTime.currentMillis()
        )
    )

    /**
     * 再生速度を更新する
     *
     * @param folderPath フォルダのパス
     * @param speed 再生速度
     */
    suspend fun updateSpeed(folderPath: String, speed: Float) = audioTapeDao.updateSpeed(
        AudioTapeSpeed(
            folderPath = folderPath,
            speed = speed,
            updateTime = SystemTime.currentMillis()
        )
    )

    /**
     * ピッチを更新する
     *
     * @param folderPath フォルダのパス
     * @param pitch ピッチ
     */
    suspend fun updatePitch(folderPath: String, pitch: Float) = audioTapeDao.updatePitch(
        AudioTapePitch(
            folderPath = folderPath,
            pitch = pitch,
            updateTime = SystemTime.currentMillis()
        )
    )

}
