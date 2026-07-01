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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ministransnaplo.app.ui.Theme
import hu.ministransnaplo.app.ui.screens.LoggedIn
import hu.ministransnaplo.app.ui.screens.LoggedInScreen
import hu.ministransnaplo.app.ui.screens.auth.Login
import hu.ministransnaplo.app.ui.screens.auth.LoginScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.ChallengeScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.MFAChallenge
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.EnrollScreen
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.MFAEnroll
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val navController = rememberNavController()

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
            }
        }
    }
}