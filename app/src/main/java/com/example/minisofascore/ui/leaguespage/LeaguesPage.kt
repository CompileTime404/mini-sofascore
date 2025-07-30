package com.example.minisofascore.ui.leaguespage

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Tournament
import com.example.minisofascore.ui.shared.SportTabRow
import com.example.minisofascore.util.getTournamentLogoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LeaguesScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: LeaguePageViewModel = hiltViewModel(),
    onTournamentClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is LeaguePageUiEvent.NavigateToTournamentDetails -> onTournamentClick(event.tournamentId)
                        LeaguePageUiEvent.NavigateBack -> onBackClick()
                        is LeaguePageUiEvent.ShowSnackbar -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaguesScreen(
        uiState = uiState,
        action = viewModel::onAction,
        onBackClick = onBackClick,
        onTournamentClick = onTournamentClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaguesScreen(
    uiState: LeaguesPageUiState,
    action: (LeaguesPageUiAction) -> Unit,
    onBackClick: () -> Unit,
    onTournamentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Leagues",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W700,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            stickyHeader {
                SportTabRow(
                    selectedSportSlug = uiState.selectedSportSlug,
                    onSportSelected = { action(LeaguesPageUiAction.SelectSport(it)) }
                )
            }
            if (uiState.isLoading){
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            } else{
                items(uiState.tournaments) { tournament ->
                    TournamentListSection(
                        tournament = tournament,
                        onTournamentClick = onTournamentClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentListSection(
    tournament: Tournament,
    onTournamentClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .padding(horizontal = 16.dp)
            .clickable(){
                onTournamentClick(tournament.id)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = getTournamentLogoUrl(tournament.id),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Text(
            tournament.name,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}