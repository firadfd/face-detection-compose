package fd.firad.face

import fd.firad.face.core.util.initKoin

//import com.mmk.kmpnotifier.notification.NotifierManager
//import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

fun startKoinIOS() {
/*
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Ios(
            showPushNotification = false,
            askNotificationPermissionOnStart = true
        )
    )
*/
    initKoin()
}
