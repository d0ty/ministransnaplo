/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import hu.ministransnaplo.app.repositories.*
import hu.ministransnaplo.app.ui.screens.auth.LoginViewModel
import hu.ministransnaplo.app.ui.screens.auth.mfa.challenge.MFAChallengeViewModel
import hu.ministransnaplo.app.ui.screens.auth.mfa.enroll.MFAEnrollViewModel
import hu.ministransnaplo.app.ui.screens.members.MembersViewModel
import hu.ministransnaplo.app.ui.screens.programs.ProgramsViewModel
import hu.ministransnaplo.app.util.AppImageLoaderFactory
import hu.ministransnaplo.app.util.SupabaseStorageFetcher
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        createSupabaseClient(Secrets.supa_url, Secrets.supa_key) {
            install(Postgrest)
            install(Auth)
            install(Functions)
            install(Storage)
            install(Coil3Integration)
        }
    }
    single<CoroutineScope>(named("appScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<AuthRepository> {
        SupabaseAuthRepository(
            supabase = get(),
            scope = get(named("appScope"))
        )
    }
    singleOf(::SupabaseMemberRepository) bind MemberRepository::class
    singleOf(::SupabaseProgramRepository) bind ProgramRepository::class
}

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MFAEnrollViewModel)
    viewModelOf(::MFAChallengeViewModel)
    viewModelOf(::MembersViewModel)
    viewModelOf(::ProgramsViewModel)
}

val imageModule = module {
    single { SupabaseStorageFetcher.Factory(get()) }
    singleOf(::AppImageLoaderFactory)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(dataModule, viewModelModule, imageModule)
    }
}