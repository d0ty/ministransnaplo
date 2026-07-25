/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */


package hu.ministransnaplo.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.screens.LoggedIn
import hu.ministransnaplo.app.ui.screens.LoggedInScreen
import hu.ministransnaplo.app.ui.screens.auth.Login
import hu.ministransnaplo.app.ui.screens.auth.LoginScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.ChallengeScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.MFAChallenge
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.EnrollScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.MFAEnroll
import hu.ministransnaplo.app.ui.screens.members.*
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(viewModel: AppViewModel = viewModel { AppViewModel() }) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    scope.launch {
        viewModel.userState.collect {
            when (it.mfaState) {
                null -> Unit
                AppViewModel.MFAState.ENROLL_REQUIRED -> navController.navigate(MFAEnroll)
                AppViewModel.MFAState.CHALLENGE_REQUIRED -> navController.navigate(MFAChallenge)
                AppViewModel.MFAState.VERIFIED -> navController.navigate(LoggedIn)
            }
        }
    }
    MaterialTheme(colorScheme = Theme.colorScheme) {
        Surface(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            contentColor = Color.White,
            color = Theme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = Login) {
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
                    ChallengeScreen(onSucess = {
                        navController.navigate(LoggedIn)
                    })
                }
                composable<Members> {
                    MembersScreen(onNavigation = {
                        navController.navigate(it)
                    })
                }
                dialog<NewMember>(dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile)) {
                    NewMemberDialog(
                        close = { navController.popBackStack() },
                        navigate = { navController.navigate(route = it) }
                    )
                }
                dialog<MemberDetail>(
                    typeMap = mapOf(typeOf<Member>() to Member.NavType),
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = !getPlatform().isMobile)
                ) { backStackEntry ->
                    val member: Member = backStackEntry.toRoute<MemberDetail>().member
                    MemberDetailDialog(
                        member,
                        close = { navController.popBackStack() },
                        navigate = { navController.navigate(it) }
                    )
                }
            }
        }
    }
}