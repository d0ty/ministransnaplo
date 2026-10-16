/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */


package hu.ministransnaplo.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import coil3.compose.setSingletonImageLoaderFactory
import hu.ministransnaplo.app.models.AuthState
import hu.ministransnaplo.app.models.MFAState
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.models.Program
import hu.ministransnaplo.app.ui.NavItem
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.prompts.*
import hu.ministransnaplo.app.ui.screens.LoggedIn
import hu.ministransnaplo.app.ui.screens.LoggedInScreen
import hu.ministransnaplo.app.ui.screens.auth.Login
import hu.ministransnaplo.app.ui.screens.auth.LoginScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.ChallengeScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.MFAChallenge
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.EnrollScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.MFAEnroll
import hu.ministransnaplo.app.ui.screens.members.*
import hu.ministransnaplo.app.ui.screens.programs.*
import hu.ministransnaplo.app.util.AppImageLoaderFactory
import hu.ministransnaplo.app.util.DbResult
import hu.ministransnaplo.app.util.SimpleNavType
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

@Serializable
object Splash : NavItem

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: AppViewModel = koinViewModel()
    val imageLoaderFactory = koinInject<AppImageLoaderFactory>()
    setSingletonImageLoaderFactory { ctx ->
        imageLoaderFactory.newImageLoader(ctx)
    }

    MaterialTheme(colorScheme = Theme.colorScheme) {
        Scaffold(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            contentColor = Color.White,
            containerColor = Theme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            NavHost(
                navController = navController,
                startDestination = Splash
            ) {
                composable<Splash> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.size(100.dp).align(Alignment.Center))
                    }
                }
                composable<Login> {
                    LoginScreen(onLoggedIn = {
                        navController.navigate(it)
                    })
                }
                composable<LoggedIn> {
                    LoggedInScreen(onNavigation = {
                        navController.navigate(it)
                    })
                }
                composable<MFAEnroll> {
                    EnrollScreen(onSucess = {
                        navController.navigate(LoggedIn)
                    })
                }
                composable<MFAChallenge> {
                    ChallengeScreen(onSuccess = {
                        navController.navigate(LoggedIn)
                    })
                }
                navigation<MembersRoot>(startDestination = Members) {
                    composable<Members> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<MembersRoot>()
                        }
                        val sharedViewModel: MembersViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        MembersScreen(onNavigation = {
                            navController.navigate(it)
                        }, sharedViewModel)
                    }
                    dialog<NewMember>(
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile)
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<MembersRoot>()
                        }
                        val sharedViewModel: MembersViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        NewMemberDialog(
                            close = { navController.popBackStack() },
                            navigate = { navController.navigate(route = it) },
                            showSnackbar = {
                                sharedViewModel.viewModelScope.launch {
                                    snackbarHostState.showSnackbar(it)
                                }
                            },
                            viewModel = sharedViewModel
                        )
                    }
                    dialog<MemberDetail>(
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile),
                        typeMap = mapOf(
                            typeOf<PromptResult?>() to SimpleNavType(PromptResult.serializer().nullable)
                        )
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<MembersRoot>()
                        }
                        val sharedViewModel: MembersViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        val entry = navController.currentBackStackEntry
                        LaunchedEffect(entry, sharedViewModel) {
                            entry?.savedStateHandle?.getStateFlow<PromptResult?>("result", null)
                                ?.collect { result ->
                                    if (result == null) return@collect;

                                    when (result) {
                                        is PromptResult.Email -> {
                                            when (result.request) {
                                                EmailPromptRequests.PROMOTE_MEMBER ->
                                                    sharedViewModel.inviteLeader(result.email) {
                                                        when (it) {
                                                            is DbResult.Success -> snackbarHostState.showSnackbar("${sharedViewModel.currentMember.value?.name} sikeresen előléptetve!")
                                                            is DbResult.Failure -> snackbarHostState.showSnackbar("Hiba az előléptetés közben!")
                                                        }
                                                    }
                                            }
                                        }

                                        is PromptResult.DestructiveAction -> {
                                            when (result.request) {
                                                DestructivePromptRequests.DELETE_MEMBER -> {
                                                    sharedViewModel.deleteMember {
                                                        when (it) {
                                                            is DbResult.Success.WithContent<*> -> {
                                                                val member: Member = it.result as Member
                                                                snackbarHostState.showSnackbar("${member.name} sikeresen törölve")
                                                            }

                                                            else -> snackbarHostState.showSnackbar("Hiba a tag törlése közben!")
                                                        }
                                                    }
                                                    navController.popBackStack()
                                                }

                                                DestructivePromptRequests.RESET_MEMBER_MFA -> {
                                                    sharedViewModel.resetLeaderMFA {
                                                        when (it) {
                                                            is DbResult.Success -> snackbarHostState.showSnackbar("${sharedViewModel.currentMember.value?.name} 2FA kulcsai sikeresen visszaállítva!")
                                                            is DbResult.Failure -> snackbarHostState.showSnackbar("Hiba a 2FA kulcsok visszaállítása közben!")
                                                        }
                                                    }
                                                }

                                                else -> {}
                                            }
                                        }
                                    }

                                    entry.savedStateHandle["result"] = null
                                }
                        }

                        MemberDetailDialog(
                            close = { navController.popBackStack() },
                            navigate = { navController.navigate(it) },
                            sharedViewModel
                        )
                    }

                }
                navigation<ProgramsRoot>(startDestination = Programs) {
                    composable<Programs> { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<ProgramsRoot>()
                        }
                        val sharedViewModel: ProgramsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ProgramsScreen(onNavigation = {
                            navController.navigate(it)
                        }, sharedViewModel)
                    }
                    dialog<NewProgram>(
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile)
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<ProgramsRoot>()
                        }
                        val sharedViewModel: ProgramsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)


                        NewProgramDialog(
                            close = { navController.popBackStack() },
                            navigate = { navController.navigate(route = it) },
                            showSnackbar = {
                                sharedViewModel.viewModelScope.launch {
                                    snackbarHostState.showSnackbar(it)
                                }
                            },
                            viewModel = sharedViewModel
                        )
                    }
                    dialog<ProgramDetails>(
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile)
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry<ProgramsRoot>()
                        }
                        val sharedViewModel: ProgramsViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        val entry = navController.currentBackStackEntry
                        LaunchedEffect(entry, sharedViewModel) {
                            entry?.savedStateHandle?.getStateFlow<PromptResult?>("result", null)
                                ?.collect { result ->
                                    println("prompt result: $result")
                                    if (result == null || result !is PromptResult.DestructiveAction) return@collect;
                                    if (result.request != DestructivePromptRequests.DELETE_PROGRAM && !result.confirmed) return@collect;
                                    println("${sharedViewModel.currentProgram.value}")
                                    sharedViewModel.deleteMember {
                                        println(it)
                                        when (it) {
                                            is DbResult.Success.WithContent<*> -> {
                                                val program = it.result as Program
                                                snackbarHostState.showSnackbar("${program.title} sikeresen törölve")
                                            }

                                            else -> snackbarHostState.showSnackbar("Hiba a program törlése közben!")
                                        }
                                    }
                                }
                        }


                        ProgramDetailDialog(
                            close = {
                                sharedViewModel.selectProgram(null)
                                navController.popBackStack()
                            },
                            navigate = { navController.navigate(route = it) },
                            viewModel = sharedViewModel
                        )
                    }

                }

                dialog<EmailPrompt>(
                    typeMap = mapOf(
                        typeOf<EmailPromptRequests>() to SimpleNavType(EmailPromptRequests.serializer()),
                        typeOf<PromptResult>() to SimpleNavType(PromptResult.serializer())
                    )
                ) { backStackEntry ->
                    val promptConfig: EmailPrompt = backStackEntry.toRoute()
                    EmailPromptDialog(
                        promptConfig,
                        finish = {
                            if (it !== null) navController.previousBackStackEntry?.savedStateHandle?.set("result", it)
                            navController.popBackStack()
                        }
                    )
                }
                dialog<DestructiveActionPrompt>(
                    typeMap = mapOf(
                        typeOf<DestructivePromptRequests>() to SimpleNavType(DestructivePromptRequests.serializer()),
                    )
                ) { backStackEntry ->
                    val promptConfig: DestructiveActionPrompt = backStackEntry.toRoute()
                    DestructiveActionPromptDialog(
                        promptConfig,
                        finish = {
                            navController.previousBackStackEntry?.savedStateHandle?.set("result", it)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.first()
        viewModel.userState.collect { authState ->
            when (authState) {
                is AuthState.Authenticated -> navController.navigate(
                    when (authState.userOrNull!!.mfaState) {
                        MFAState.ENROLL_REQUIRED -> MFAEnroll
                        MFAState.CHALLENGE_REQUIRED -> MFAChallenge
                        MFAState.VERIFIED -> LoggedIn
                        null -> LoggedIn
                    }
                ) {
                    popUpTo<Splash> { inclusive = true }
                    launchSingleTop = true
                }

                is AuthState.Unauthenticated -> navController.navigate(Login) {
                    popUpTo(navController.currentDestination?.parent?.id ?: return@navigate) { inclusive = true }
                    launchSingleTop = true
                }

                AuthState.Loading -> {}
            }
        }
    }

}