package com.example.minisofascore.di

import com.example.minisofascore.data.repository.EventsRepository
import com.example.minisofascore.data.repository.EventsRepositoryImpl
import com.example.minisofascore.data.repository.IncidentsRepository
import com.example.minisofascore.data.repository.IncidentsRepositoryImpl
import com.example.minisofascore.data.repository.PlayerRepository
import com.example.minisofascore.data.repository.PlayerRepositoryImpl
import com.example.minisofascore.data.repository.TournamentRepository
import com.example.minisofascore.data.repository.TournamentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{

    @Binds
    @Singleton
    abstract fun bindEventsRepository(
        eventsRepositoryImpl: EventsRepositoryImpl
    ) : EventsRepository

    @Binds
    @Singleton
    abstract fun bindTournamentRepository(
        tournamentRepositoryImpl: TournamentRepositoryImpl
    ): TournamentRepository

    @Binds
    @Singleton
    abstract fun bindIncidentsRepository(
        incidentsRepositoryImpl: IncidentsRepositoryImpl
    ): IncidentsRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ) : PlayerRepository
}