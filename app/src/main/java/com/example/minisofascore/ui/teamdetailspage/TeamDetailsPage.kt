package com.example.minisofascore.ui.teamdetailspage

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.minisofascore.data.model.Event
import com.example.minisofascore.ui.shared.TopBar
import com.example.minisofascore.util.getManagerImageUrl
import com.example.minisofascore.util.getPlayerImageUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun TeamDetailsPageRoot(
    teamId: Int,
    onBackClick: () -> Unit,
    onPlayerClick: (Int) -> Unit,
    onEventClick: (Int) -> Unit,
    onTournamentClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeamDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
            withContext(Dispatchers.Main.immediate){
                viewModel.uiEvent.collect {
                    when(it){
                        is TeamDetailsUiEvent.NavigateBack -> onBackClick()
                        is TeamDetailsUiEvent.NavigateToPlayerDetails -> onPlayerClick(it.playerId)
                        is TeamDetailsUiEvent.NavigateToEventDetails -> onEventClick(it.eventId)
                        is TeamDetailsUiEvent.NavigateToTournamentDetails -> onTournamentClick(it.tournamentId)
                        is TeamDetailsUiEvent.ShowSnackbar -> {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.teamEvents.collectAsLazyPagingItems()
    TeamDetailsPage(
        uiState = state,
        onAction = viewModel::onAction,
        listOfEvents = lazyPagingItems,
        modifier = modifier
    )
}

@Composable
fun TeamDetailsPage(
    uiState: TeamDetailsUiState,
    onAction: (TeamDetailsUiAction) -> Unit,
    listOfEvents: LazyPagingItems<Event>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                tournament = null,
                team = uiState.team,
                onBackClick = { onAction(TeamDetailsUiAction.ClickBackButton) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            item {
                Text(
                    text = uiState.team?.managerName.toString(),
                    modifier = Modifier.padding(16.dp)
                )
                AsyncImage(
                    model = getManagerImageUrl(uiState.team?.managerName ?: "Unknown"),
                    contentDescription = null
                )
            }
        }
    }
}
