package fd.firad.face.di

import com.russhwolf.settings.Settings
import fd.firad.face.core.network.NetworkCaller
import fd.firad.face.core.network.NetworkErrorHandler
import fd.firad.face.core.storage.StorageService
import fd.firad.face.core.theme.ThemeViewModel
import fd.firad.face.features.employees.data.repository.EmployeeRepositoryImpl
import fd.firad.face.features.employees.domain.repository.EmployeeRepository
import fd.firad.face.features.employees.domain.usecase.GetEmployeesUseCase
import fd.firad.face.features.employees.presentation.EmployeesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlinx.serialization.json.Json

val networkModule = module {
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single { NetworkErrorHandler() }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        print(message)
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 80000
                connectTimeoutMillis = 80000
                socketTimeoutMillis = 80000
            }
        }
    }

    single { NetworkCaller(get(), get(), get()) }
}

val appModule = module {
    single { Settings() }
    single { StorageService(get()) }
    single { ThemeViewModel(get()) }

    // Employees Feature
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
    single { GetEmployeesUseCase(get()) }
    viewModel { EmployeesViewModel(get()) }
}

expect val platformModule: org.koin.core.module.Module

val sharedModule = listOf(networkModule, appModule, platformModule)
