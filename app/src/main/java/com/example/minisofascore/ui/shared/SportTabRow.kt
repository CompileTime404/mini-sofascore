package com.example.minisofascore.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsFootball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SportTabRow(
    selectedSportSlug: String,
    onSportSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    data class Sport(val slug: String, val label: String, val icon: ImageVector)

    val colors = MaterialTheme.colorScheme

    val sports = remember {
        listOf(
            Sport("football", "Football", Icons.Outlined.SportsSoccer),
            Sport("basketball", "Basketball", Icons.Outlined.SportsBasketball),
            Sport("american-football", "Am. Football", Icons.Outlined.SportsFootball)
        )
    }

    val selectedIndex = sports.indexOfFirst { it.slug == selectedSportSlug }.coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = colors.primary,
        divider = {},
        indicator = { tabPositions ->
            val tabPos = tabPositions[selectedIndex]
            Box(
                Modifier
                    .tabIndicatorOffset(tabPos)
                    .height(4.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(colors.surface, RoundedCornerShape(1.5.dp))
            )
        }
    ) {
        sports.forEachIndexed { index, sport ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSportSelected(sport.slug) },
                selectedContentColor = colors.surface,
                unselectedContentColor = colors.surface,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = sport.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sport.label,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        color = colors.surface
                    )
                }
            }

        }
    }
}