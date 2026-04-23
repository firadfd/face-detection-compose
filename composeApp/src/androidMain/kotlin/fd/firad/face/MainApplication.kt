package fd.firad.face

import android.app.Application
import fd.firad.face.core.util.initKoin
//import com.mmk.kmpnotifier.notification.NotifierManager
//import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
/*
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                showPushNotification = true,
                notificationIconResId = R.mipmap.ic_launcher, // Default icon
            )
        )
*/

        initKoin {
            androidContext(this@MainApplication)
        }
    }
}
