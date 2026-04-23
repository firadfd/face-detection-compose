package fd.firad.face.di

import fd.firad.face.IosLocalNotifier
import fd.firad.face.LocalNotifier
import org.koin.dsl.module

actual val platformModule = module {
    single<LocalNotifier> { IosLocalNotifier() }
}
