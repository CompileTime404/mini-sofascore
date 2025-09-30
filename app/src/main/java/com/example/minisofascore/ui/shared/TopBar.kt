package com.example.minisofascore.ui.shared

import android.R.attr.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Team3
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.util.getCountryFlagUrl
import com.example.minisofascore.util.getTeamLogoUrl
import com.example.minisofascore.util.getTournamentLogoUrl

@Composable
fun TopBar(
    tournament: Tournament?,
    team: Team3?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .background(colors.primary)
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.onPrimary
            )
        }

        Row {
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (team == null ) {
                        getTournamentLogoUrl(tournament?.id ?: -1)
                    } else{
                        getTeamLogoUrl(team.id)
                    },
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = tournament?.name ?: (team?.name ?: "Unknown"),
                    color = colors.onPrimary,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp
                )
                Row {
                    AsyncImage(
                        model = if (team == null ) {
                            getCountryFlagUrl(tournament?.country?.name ?: "Unknown")
                        } else{
                            getCountryFlagUrl(team.country.name)
                        },
                        contentDescription = "Flag",
                        modifier = Modifier
                            .size(16.dp)
                            .clip(shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tournament?.country?.name ?: (team?.country?.name ?: "Unknown"),
                        color = colors.onPrimary,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }
}