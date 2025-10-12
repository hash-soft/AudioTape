package com.hashsoft.audiotape.data

import com.hashsoft.audiotape.logic.SystemTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * オーディオテープのデータ操作を行うリポジトリ
 *
 * @param audioTapeDao オーディオテープのDAO
 */
class AudioTapeRepository(private val audioTapeDao: AudioTapeDao) {

    /**
     * すべてのオーディオテープを取得する
     *
     * @return オーディオテープのリストをFlowで返す
     */
    fun getAll(): Flow<List<AudioTapeDto>> {
        return audioTapeDao.getAll().map { list ->
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
    fun findByPath(path: String): Flow<AudioTapeDto> {
        return audioTapeDao.findByPath(path).map {
            if (it == null) {
                // このフォルダはdbにまだ存在していない
                Timber.i("findByPath is null: $path")
                AudioTapeDto("", "")
            } else {
                Timber.d("audio tape: $it")
                convertEntityToDto(it)
            }
        }
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
            repeat = entity.repeat,
            volume = entity.volume,
            speed = entity.speed,
            pitch = entity.pitch,
            itemCount = entity.itemCount,
            totalTime = entity.totalTime,
            createTime = entity.createTime,
            updateTime = entity.updateTime
        )
    }

    /**
     * 新しいオーディオテープを挿入する
     *
     * @param folderPath フォルダのパス
     * @param currentName 現在のアイテム名
     * @param position 再生位置
     * @param sortOrder ソート順
     * @return 挿入した行のID
     */
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

    /**
     * 再生位置を更新する
     *
     * @param folderPath フォルダのパス
     * @param currentName 現在のアイテム名
     * @param position 再生位置
     */
    suspend fun updatePlayingPosition(folderPath: String, currentName: String, position: Long) =
        audioTapeDao.updatePlayingPosition(
            AudioTapePlayingPosition(
                folderPath = folderPath,
                currentName = currentName,
                position = position,
                updateTime = SystemTime.currentMillis()
            )
        )

    /**
     * [AudioTapeDto]が有効かどうかを判定する
     *
     * @param dto 判定対象のDTO
     * @return 有効な場合はtrue
     */
    fun validAudioTapeDto(dto: AudioTapeDto): Boolean {
        return dto.folderPath.isNotEmpty() && dto.currentName.isNotEmpty()
    }

}
