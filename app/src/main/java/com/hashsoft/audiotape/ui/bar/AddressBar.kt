package com.hashsoft.audiotape.ui.bar


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.LocationType
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.theme.AddressBarItemSpace
import com.hashsoft.audiotape.ui.theme.AddressBarStartPadding
import com.hashsoft.audiotape.ui.theme.AddressRightIconSize
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.addressBarBackgroundColor
import com.hashsoft.audiotape.ui.theme.addressBarContentColor
import com.hashsoft.audiotape.ui.theme.primaryItemColor
import kotlinx.coroutines.launch


@Composable
fun AddressBar(addressList: List<StorageLocationDto>, onAddressChange: (String) -> Unit = {}) {
    Surface(
        contentColor = addressBarContentColor,
        color = addressBarBackgroundColor,
        modifier = Modifier
            .scrollable(rememberScrollableState { it }, Orientation.Horizontal)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = AddressBarStartPadding)
        ) {
            Box(modifier = Modifier.weight(1F)) {
                AddressRow(addressList, onAddressChange)
            }
            val upEnabled = addressList.size > 1
            UpButton(upEnabled) {
                // addressListから一つ前のパスに移動する
                if (upEnabled) {
                    val address = addressList.getOrNull(addressList.size - 2) ?: return@UpButton
                    onAddressChange(address.path)
                } else {
                    onAddressChange("")
                }
            }
        }
    }
}

@Composable
private fun AddressRow(
    addressList: List<StorageLocationDto>,
    onAddressChange: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(addressList) {
        // 初期位置
        listState.scrollToItem(index = addressList.size - 1)
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AddressBarItemSpace),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
    ) {
        items(addressList.size) {
            val address = addressList.getOrNull(it) ?: return@items
            Row(
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            listState.scrollToItem(index = it)
                            onAddressChange(address.path)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically

            ) {
                OneDirectory(address, it != 0, it == addressList.lastIndex)
            }
        }

    }

}

@Composable
private fun OneDirectory(
    address: StorageLocationDto,
    hasRight: Boolean,
    isPrimary: Boolean = false
) {
    if (hasRight) {
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = stringResource(R.string.chevron_right),
            modifier = Modifier.size(AddressRightIconSize)
        )
    }
    when (address.type) {
        LocationType.Root -> {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = address.name,
                tint = if (isPrimary) primaryItemColor else LocalContentColor.current
            )
        }

        LocationType.Inner -> {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = address.name,
                tint = if (isPrimary) primaryItemColor else LocalContentColor.current
            )
        }

        LocationType.External -> {
            Icon(
                imageVector = Icons.Default.SdCard,
                contentDescription = address.name,
                tint = if (isPrimary) primaryItemColor else LocalContentColor.current
            )
        }

        LocationType.Normal -> {
            if (isPrimary) {
                Text(text = address.name, color = primaryItemColor)
            } else {
                Text(text = address.name)
            }
        }
    }

}

@Composable
private fun UpButton(enabled: Boolean = true, onAddressChange: () -> Unit = {}) {
    IconButton(
        onClick = { onAddressChange() },
        enabled = enabled,
        content = {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                null,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddressBarPreview() {
    AudioTapeTheme {
        AddressBar(
            addressList = listOf(
                StorageLocationDto("test1", "", LocationType.Root),
                StorageLocationDto("test2", "", LocationType.Inner),
                StorageLocationDto("test3", "", LocationType.Normal),
                StorageLocationDto("3テスト4d", "", LocationType.Normal),
                StorageLocationDto("d日本語d", "", LocationType.Normal),
                StorageLocationDto("test_test_test_d", "", LocationType.Normal),
                StorageLocationDto("last_name", "", LocationType.Normal)
            )
        )
    }
}