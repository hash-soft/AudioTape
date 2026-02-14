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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.BuildConfig
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.Route
import com.hashsoft.audiotape.ui.dialog.HelpDialog
import com.hashsoft.audiotape.ui.dialog.PrivacyPolicyDialog
import com.hashsoft.audiotape.ui.theme.AboutBorder
import com.hashsoft.audiotape.ui.theme.AboutBorderHorizonalPadding
import com.hashsoft.audiotape.ui.theme.AboutBorderVerticalPadding
import com.hashsoft.audiotape.ui.theme.AboutCopyrightSpacerHeight
import com.hashsoft.audiotape.ui.theme.AboutHorizonalPadding
import com.hashsoft.audiotape.ui.theme.AboutIconElevation
import com.hashsoft.audiotape.ui.theme.AboutIconPadding
import com.hashsoft.audiotape.ui.theme.AboutIconSize
import com.hashsoft.audiotape.ui.theme.AboutSpacerHeight
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutView(
    versionName: String = BuildConfig.VERSION_NAME,
    viewModel: AboutViewModel = viewModel(),
    onTransferClick: (route: Any) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    when (screenState) {
        AboutScreenState.HowtoUseDialog -> {
            HelpDialog { viewModel.setScreenState(AboutScreenState.Normal) }
        }

        AboutScreenState.PrivacyPolicyDialog -> {
            PrivacyPolicyDialog { viewModel.setScreenState(AboutScreenState.Normal) }
        }

        else -> {}
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = AboutIconElevation),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.ic_launcher_background))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_icon_description),
                    modifier = Modifier
                        .size(AboutIconSize)
                        .padding(AboutIconPadding)
                )
            }

            Spacer(modifier = Modifier.height(AboutSpacerHeight))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Version $versionName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(AboutSpacerHeight))

            Text(
                text = stringResource(R.string.this_app_description),
                modifier = Modifier.padding(horizontal = AboutHorizonalPadding),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(AboutSpacerHeight))
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = AboutBorderHorizonalPadding,
                    vertical = AboutBorderVerticalPadding
                ),
                thickness = AboutBorder,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.help_title)) },
                supportingContent = { Text(stringResource(R.string.help_supporting)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_help),
                        contentDescription = stringResource(R.string.help_description)
                    )
                },
                modifier = Modifier.clickable { viewModel.setScreenState(AboutScreenState.HowtoUseDialog) }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.licenses_title)) },
                leadingContent = {
                    Icon(
                        painterResource(id = android.R.drawable.ic_menu_info_details),
                        contentDescription = stringResource(R.string.licenses_description)
                    )
                },
                modifier = Modifier.clickable { onTransferClick(Route.License) }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.privacy_policy_title)) },
                leadingContent = {
                    Icon(
                        painterResource(id = android.R.drawable.ic_menu_view),
                        contentDescription = stringResource(R.string.privacy_policy_description)
                    )
                },
                modifier = Modifier.clickable { viewModel.setScreenState(AboutScreenState.PrivacyPolicyDialog) }
            )

            Spacer(modifier = Modifier.height(AboutCopyrightSpacerHeight))

            Text(
                text = stringResource(R.string.copyright),
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