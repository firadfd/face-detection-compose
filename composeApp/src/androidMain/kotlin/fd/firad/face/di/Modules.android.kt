package fd.firad.face.di

import fd.firad.face.AndroidLocalNotifier
import fd.firad.face.LocalNotifier
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<LocalNotifier> { AndroidLocalNotifier(androidContext()) }
}
