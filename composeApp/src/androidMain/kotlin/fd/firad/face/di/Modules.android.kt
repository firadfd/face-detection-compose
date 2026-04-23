package fd.firad.face.di

import fd.firad.face.core.util.AndroidLocalNotifier
import fd.firad.face.core.util.LocalNotifier
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<LocalNotifier> { AndroidLocalNotifier(androidContext()) }
}
