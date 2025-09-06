package com.example.directorytest.ui.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.data.LocationType
import com.hashsoft.audiotape.data.StorageLocationDto
import kotlinx.coroutines.launch


@Composable
fun AddressBar(addressList: List<StorageLocationDto>, onAddressChange: (String) -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Red)
            .scrollable(rememberScrollableState { it }, Orientation.Horizontal)
            .draggable(
                interactionSource = remember { MutableInteractionSource() },
                state = remember { DraggableState {} },
                orientation = Orientation.Horizontal,
            ),
    ) {
        Box(
            modifier = Modifier.weight(1F),
        ) {
            AddressRow(addressList, onAddressChange)
        }
        val upEnabled = addressList.size > 1
        UpButton(upEnabled) {
            // addressListから一つ前のパスに移動する
            if (upEnabled) {
                onAddressChange(addressList[addressList.size - 2].path)
            } else {
                onAddressChange("")
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

    LaunchedEffect(Unit) {
        // 初期位置
        listState.scrollToItem(index = addressList.size - 1)
    }

    LazyRow(
        modifier = Modifier.background(Color.Black),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        state = listState
    ) {
        items(addressList.size) {
            val address = addressList[it]
            Box(
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            listState.scrollToItem(index = it)
                            //listState.animateScrollToItem(index = 10, scrollOffset = 0)
                            onAddressChange(address.path)
                        }
                    }
                    //.height(32.dp)
                    .background(Color.Cyan),
                contentAlignment = Alignment.Center
            ) {
                when (address.type) {
                    LocationType.Root -> {
                        Image(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                        )
                    }

                    LocationType.Home -> {
                        Image(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                        )
                    }

                    LocationType.Normal -> {
                        Text(address.name)
                    }
                }

            }
        }


    }
}

@Composable
private fun UpButton(enabled: Boolean = true, onAddressChange: () -> Unit = {}) {
//    Box(
//        modifier = Modifier
//            .clickable { onAddressChange() },
//    ) {
//        Icon(
//            imageVector = Icons.Default.ArrowUpward,
//            null,
//        )
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
    // }
}

@Preview(showBackground = true)
@Composable
fun AddressBarPreview() {
//    AddressBar(
//        addresses = listOf(
//            AddressData("test1", ""),
//            AddressData("test2", ""),
//            AddressData("test3", "")
//        )
//    )
}