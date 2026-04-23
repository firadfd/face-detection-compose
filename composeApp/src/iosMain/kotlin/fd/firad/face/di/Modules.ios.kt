package fd.firad.face.di

import fd.firad.face.core.util.IosLocalNotifier
import fd.firad.face.core.util.LocalNotifier
import org.koin.dsl.module

actual val platformModule = module {
    single<LocalNotifier> { IosLocalNotifier() }
}
