package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.bar.TopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayHomeRoute(audioTape: AudioTapeDto) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                audioTape.currentName, TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.outlineVariant,
                )
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // 引数でDB情報を渡してviewModelのinitで作成するのがいいか
            // conflict発生（作成済み）の場合になにもしない処理で
            // なのでviewModelにrepositoryとDB情報を渡して
            // initでcreate、flowでDB情報を取得というのがよさそう
            // 外部からの初期値が必要なもの
            // パス、再生中の曲（フォルダからの場合、選択した曲が初期再生曲になる）
            // これがあるからupsertせんといかんか
            //LibrarySheetRoute()
            AudioPlayViewRoute(audioTape)
        }
    }
}
