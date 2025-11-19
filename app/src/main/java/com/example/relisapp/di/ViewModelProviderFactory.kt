package com.example.relisapp.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.data.local.SessionManager
import com.example.relisapp.viewmodel.AuthViewModelFactory

object ViewModelProviderFactory {
    fun provideAuthViewModelFactory(context: Context): AuthViewModelFactory {
        val repo = ServiceLocator.provideUserRepository(context)
        val session = SessionManager(context)
        return AuthViewModelFactory(repo, session)
    }
}

