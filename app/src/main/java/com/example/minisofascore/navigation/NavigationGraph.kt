package com.example.minisofascore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.minisofascore.navigation.NavigationGraph.EventDetailsPage
import com.example.minisofascore.navigation.NavigationGraph.MainListPage
import com.example.minisofascore.ui.eventdetailspage.EventDetailsScreenRoot
import com.example.minisofascore.ui.leaguespage.LeaguesScreenRoot
import com.example.minisofascore.ui.mainlistpage.MainListScreenRoot
import com.example.minisofascore.ui.playedetailspage.PlayerDetailsScreenRoot
import com.example.minisofascore.ui.settingspage.SettingsScreenRoot
import com.example.minisofascore.ui.teamdetailspage.TeamDetailsPageRoot
import com.example.minisofascore.ui.tournamentdetailspage.TournamentDetailsScreenRoot
import kotlinx.serialization.Serializable

sealed interface NavigationGraph {

    @Serializable
    data object MainListPage : NavigationGraph

    @Serializable
    data object LeaguesPage : NavigationGraph

    @Serializable
    data object SettingsPage : NavigationGraph

    @Serializable
    data class TeamDetailsPage(val teamId: Int) : NavigationGraph

    @Serializable
    data class PlayerDetailsPage(val playerId: Int) : NavigationGraph

    @Serializable
    data class TournamentDetailsPage(val tournamentId: Int) : NavigationGraph

    @Serializable
    data class EventDetailsPage(val eventId: Int) : NavigationGraph
}

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MainListPage,
        modifier = modifier
    ) {
        composable<NavigationGraph.LeaguesPage> {
            LeaguesScreenRoot(
                onBackClick = { navController.popBackStack() },
                onTournamentClick = {
                    navController.navigate(NavigationGraph.TournamentDetailsPage(it))
                }
            )
        }
        composable<NavigationGraph.TeamDetailsPage> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<NavigationGraph.TeamDetailsPage>()
            TeamDetailsPageRoot(
                teamId = args.teamId,
                onBackClick = {
                    navController.popBackStack()
                },
                onPlayerClick = {
                    navController.navigate(NavigationGraph.PlayerDetailsPage(it))
                },
                onEventClick = {
                    navController.navigate(NavigationGraph.EventDetailsPage(it))
                },
                onTournamentClick = {
                    navController.navigate(NavigationGraph.TournamentDetailsPage(it))
                }
            )
        }
        composable<NavigationGraph.PlayerDetailsPage> {navBackStackEntry ->
            val args = navBackStackEntry.toRoute<NavigationGraph.PlayerDetailsPage>()
            PlayerDetailsScreenRoot(
                playerId = args.playerId,
                onBackClick = {
                    navController.popBackStack()
                },
                onTournamentClick = {
                    navController.navigate(NavigationGraph.TournamentDetailsPage(it))
                },
                onTeamClick = {
                    navController.navigate(NavigationGraph.TeamDetailsPage(it))
                },
                onEventClick = {
                    navController.navigate(EventDetailsPage(it))
                },
            )
        }
        composable<NavigationGraph.SettingsPage> {
            SettingsScreenRoot(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavigationGraph.TournamentDetailsPage> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<NavigationGraph.TournamentDetailsPage>()
            TournamentDetailsScreenRoot(
                tournamentId = args.tournamentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onTeamClick = {
                    navController.navigate(NavigationGraph.TeamDetailsPage(it))
                },
                onEventClick = {
                    navController.navigate(EventDetailsPage(it))
                }
            )
        }
        composable<EventDetailsPage> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<EventDetailsPage>()
            EventDetailsScreenRoot(
                eventId = args.eventId,
                onBackClick = {
                    navController.popBackStack()
                },
                onTeamClick = {
                    navController.navigate(NavigationGraph.TeamDetailsPage(it))
                },
                onPlayerClick = {
                    navController.navigate(NavigationGraph.PlayerDetailsPage(it))
                },
                onTournamentClick = {
                    navController.navigate(NavigationGraph.TournamentDetailsPage(it))
                }
            )
        }
        composable<MainListPage> {
            MainListScreenRoot(
                onEventClick = { eventId ->
                    navController.navigate(EventDetailsPage(eventId))
                },
                onTournamentClick = { tournamentId ->
                    navController.navigate(NavigationGraph.TournamentDetailsPage(tournamentId))
                },
                onSettingsClick = {
                    navController.navigate(NavigationGraph.SettingsPage)
                },
                onLeaguesClick = {
                    navController.navigate(NavigationGraph.LeaguesPage)
                }
            )
        }
    }
}



















