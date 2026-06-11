package com.example.splitexpenses.di

import com.example.splitexpenses.data.repository.AuthRepository
import com.example.splitexpenses.data.repository.GroupRepository
import com.example.splitexpenses.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single { AuthRepository(get(), get()) }
    single { GroupRepository(get(), get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { GroupsViewModel(get(), get()) }
    viewModel { GroupDetailViewModel(get(), get()) }
    viewModel { ExpenseViewModel() }
    viewModel { ProfileViewModel(get()) }
    viewModel { SettlementViewModel() }
    viewModel { ExploreViewModel() }
}
