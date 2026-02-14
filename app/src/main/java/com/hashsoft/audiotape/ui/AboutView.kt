package com.hashsoft.audiotape.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashsoft.audiotape.BuildConfig
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.Route
import com.hashsoft.audiotape.ui.dialog.HelpDialog
import com.hashsoft.audiotape.ui.dialog.PrivacyPolicyDialog
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutView(
    versionName: String = BuildConfig.VERSION_NAME,
    onTransferClick: (route: Any) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val showHelpDialog = remember { mutableStateOf(false) }
    val showPrivacyPolicy = remember { mutableStateOf(false) }

    if (showHelpDialog.value) {
        HelpDialog { showHelpDialog.value = false }
    }
    if (showPrivacyPolicy.value) {
        PrivacyPolicyDialog { showPrivacyPolicy.value = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.about_label)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.ic_launcher_background))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // アプリ名
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // バージョン
            Text(
                text = "Version $versionName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(32.dp))

            // アプリの説明
            Text(
                text = "フォルダごとに再生位置を記憶する、カセットテープ風のオーディオプレイヤーです。語学学習や長い音声ファイルの管理に最適です。",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("アプリの操作方法") },
                supportingContent = { Text("再生・フォルダ管理・記憶機能について") },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_help),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { showHelpDialog.value = true }
            )

            // メニュー項目
            ListItem(
                headlineContent = { Text(stringResource(R.string.licenses_title)) },
                leadingContent = {
                    Icon(
                        painterResource(id = android.R.drawable.ic_menu_info_details),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { onTransferClick(Route.License) }
            )

            ListItem(
                headlineContent = { Text("プライバシーポリシー") },
                leadingContent = {
                    Icon(
                        painterResource(id = android.R.drawable.ic_menu_view),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { showPrivacyPolicy.value = true }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 著作権表示
            Text(
                text = "© 2026 Your Name",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AboutViewPreview() {
    AudioTapeTheme {
        AboutView()
    }
}