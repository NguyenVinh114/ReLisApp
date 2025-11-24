package com.example.relisapp.nam.di
import android.content.Context
import com.example.relisapp.nam.viewmodel.AuthViewModelFactory

object ViewModelProviderFactory {

    fun provideAuthViewModelFactory(context: Context): AuthViewModelFactory {
        val repo = ServiceLocator.provideUserRepository(context)
        val session = ServiceLocator.provideSessionManager(context)
        return AuthViewModelFactory(repo, session)
    }
}
