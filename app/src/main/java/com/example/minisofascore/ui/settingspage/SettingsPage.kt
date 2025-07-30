package com.example.minisofascore.ui.settingspage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.minisofascore.R
import com.example.minisofascore.ui.theme.MiniSofascoreTheme

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsPageViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    SettingsScreen(
        isDarkTheme = isDarkTheme,
        onToggleDarkTheme = viewModel::toggleDarkTheme,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W700,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary
                ),
                modifier = Modifier
                    .background(colors.primary)
                    .statusBarsPadding()
                    .height(48.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
        ) {
            item {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = colors.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    ) {
                        TextField(
                            value = "English",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Language",
                                    color = colors.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.W700,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = colors.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                lineHeight = 20.sp,
                                letterSpacing = 0.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                HorizontalDivider(color = colors.onBackground.copy(alpha = 0.1f))
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Theme",
                            fontSize = 12.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.W700,
                            lineHeight = 16.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Column {
                            listOf("Light" to false, "Dark" to true).forEach { (label, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        color = colors.onBackground,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        letterSpacing = 0.sp,
                                        lineHeight = 20.sp
                                    )
                                    RadioButton(
                                        selected = isDarkTheme == value,
                                        onClick = { onToggleDarkTheme(value) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colors.primary,
                                            unselectedColor = colors.onBackground.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = colors.onBackground.copy(alpha = 0.1f))
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Date Format",
                            fontSize = 12.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.W700,
                            lineHeight = 16.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Column {
                            listOf(
                                "DD / MM / YYYY" to true,
                                "MM / DD / YYYY" to false
                            ).forEach { (label, selected) ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        color = colors.onBackground,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        lineHeight = 20.sp,
                                        letterSpacing = 0.sp
                                    )
                                    RadioButton(
                                        selected = selected,
                                        onClick = null,
                                        modifier = Modifier.padding(end = 12.dp),
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colors.primary,
                                            unselectedColor = colors.onBackground.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = colors.onBackground.copy(alpha = 0.1f))
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "About",
                            fontWeight = FontWeight.W700,
                            fontSize = 20.sp,
                            color = colors.onBackground,
                            lineHeight = 28.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Sofascore Android Academy",
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            color = colors.onBackground,
                            lineHeight = 20.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Class 2025",
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            color = colors.onBackground,
                            lineHeight = 20.sp,
                            letterSpacing = 0.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = colors.onBackground.copy(alpha = 0.1f))
                        Spacer(Modifier.height(16.dp))

                        listOf(
                            "App Name" to "Mini Sofascore App",
                            "API Credit" to "Sofascore",
                            "Developer" to "Dorian Banić"
                        ).forEach { (label, value) ->
                            Text(
                                text = label,
                                color = colors.onBackground.copy(alpha = 0.4f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W700,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.sp
                            )
                            Text(
                                text = value,
                                color = colors.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W400,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = 0.sp
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.2f))
                    }
                }
            }

            item {
                Icon(
                    painter = painterResource(id = R.drawable.sofascore_lockup),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().wrapContentSize(align = Alignment.Center),
                    tint = colors.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    MiniSofascoreTheme {
        SettingsScreen(
            isDarkTheme = false,
            onToggleDarkTheme = {},
            onBackClick = {}
        )
    }
}
