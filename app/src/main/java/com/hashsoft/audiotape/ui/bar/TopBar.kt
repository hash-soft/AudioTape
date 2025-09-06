package com.hashsoft.audiotape.ui.bar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun TopBar(title:String, colors:TopAppBarColors = TopAppBarDefaults.topAppBarColors()){
    TopAppBar(title = { Text(text = title)}, colors = colors)
}