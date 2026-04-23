package fd.firad.face

import androidx.compose.ui.window.ComposeUIViewController
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration


fun MainViewController() = ComposeUIViewController { App() }

fun initKoinIOS() {
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = true
        )
    )
    initKoin()
}