package com.hashsoft.audiotape.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R

/**
 * テープが存在していない場合に表示するビュー
 */
@Composable
fun NoTapeView() {
    Text(text = stringResource(id = R.string.no_tape_warn))
}
