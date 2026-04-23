package fd.firad.face.di

import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

import fd.firad.face.core.theme.ThemeViewModel

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}

val appModule = module {
    single { Settings() }
    single { ThemeViewModel(get()) }
}

expect val platformModule: org.koin.core.module.Module

val sharedModule = listOf(networkModule, appModule, platformModule)
