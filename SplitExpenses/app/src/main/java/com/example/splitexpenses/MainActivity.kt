// FILE: MainActivity.kt
package com.example.splitexpenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject
import com.example.splitexpenses.data.repository.AuthRepository
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitexpenses.ui.*
import com.example.splitexpenses.viewmodel.*

class MainActivity : ComponentActivity() {
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // ViewModels PARTILHADOS entre múltiplos ecrãs ficam no escopo da Activity
                    // Assim conseguem comunicar entre si (ex: Dashboard <-> CreateGroup)
                    val groupsViewModel: GroupsViewModel = koinViewModel()
                    val groupDetailViewModel: GroupDetailViewModel = koinViewModel()
                    val expenseViewModel: ExpenseViewModel = koinViewModel()

                    // Se o utilizador já está autenticado, vai direto para o dashboard
                    val startDestination = if (authRepository.currentUser != null) "dashboard" else "login"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            val loginViewModel: LoginViewModel = koinViewModel()
                            LoginScreenComp(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    // Atualizar o user nos ViewModels partilhados após login
                                    groupsViewModel.refreshCurrentUser()
                                    // IMPORTANTE: recarregar grupos para o novo utilizador
                                    // (cancela o listener antigo e regista um novo)
                                    groupsViewModel.reloadGroups()
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }

                        composable("register") {
                            // ESCOPO LOCAL: O RegisterViewModel só existe enquanto este ecrã estiver aberto
                            val registerViewModel: RegisterViewModel = koinViewModel()
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    // Atualizar o user nos ViewModels partilhados após registo
                                    groupsViewModel.refreshCurrentUser()
                                    // IMPORTANTE: recarregar grupos para o novo utilizador
                                    groupsViewModel.reloadGroups()
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            GroupsListScreen(
                                viewModel = groupsViewModel,
                                onNavigateToGroup = { id -> navController.navigate("group/$id") },
                                onNavigateToCreateGroup = { navController.navigate("create-group") },
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToExplore = { navController.navigate("explore") },
                                onNavigateToSettlements = { navController.navigate("settlements") }
                            )
                        }

                        composable("create-group") {
                            CreateGroupScreen(
                                viewModel = groupsViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("group/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            GroupDetailScreen(
                                groupId = id,
                                viewModel = groupDetailViewModel,
                                onBack = { navController.popBackStack() },
                                onNavigateToNewExpense = {
                                    expenseViewModel.setGroupMembers(groupDetailViewModel.uiState.value.members)
                                    navController.navigate("new-expense")
                                }
                            )
                        }

                        composable("new-expense") {
                            NewExpenseScreen(
                                viewModel = expenseViewModel,
                                onBack = { navController.popBackStack() },
                                onSave = {
                                    expenseViewModel.getCreatedExpense()?.let {
                                        groupDetailViewModel.addExpense(it)
                                    }
                                    expenseViewModel.clearForm()
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("settlements") {
                            // ESCOPO LOCAL
                            val settlementViewModel: SettlementViewModel = koinViewModel()
                            SettlementScreen(
                                viewModel = settlementViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("explore") {
                            // ESCOPO LOCAL
                            val exploreViewModel: ExploreViewModel = koinViewModel()
                            ExploreScreen(
                                viewModel = exploreViewModel,
                                onNavigateToDashboard = { navController.navigate("dashboard") }
                            )
                        }

                        composable("profile") {
                            // ESCOPO LOCAL
                            val profileViewModel: ProfileViewModel = koinViewModel()
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onBack = {
                                    // Atualizar o nome no dashboard quando voltar do perfil
                                    groupsViewModel.refreshCurrentUser()
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    // Fazer sign out do Firebase antes de navegar para o login
                                    authRepository.signOut()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
