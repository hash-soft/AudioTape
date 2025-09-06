package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageLocationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddressBarState(
    private val _storageAddressRepository: StorageAddressRepository,
    private val _list: MutableStateFlow<List<StorageLocationDto>> = MutableStateFlow(
        emptyList()
    )
) {
    val list: StateFlow<List<StorageLocationDto>> = _list.asStateFlow()

    fun load(path: String) {
        _list.update { _storageAddressRepository.pathToStorageLocationList(path) }
    }

}



