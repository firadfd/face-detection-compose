package fd.firad.face

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNTimeIntervalNotificationTrigger

class IosLocalNotifier : LocalNotifier {
    override fun notify(title: String, message: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        // Request permission if needed
        center.requestAuthorizationWithOptions(
            options = 7U // Alert + Sound + Badge
        ) { granted, error ->
            if (granted) {
                val content = UNMutableNotificationContent()
                content.setTitle(title)
                content.setBody(message)
                content.setSound(platform.UserNotifications.UNNotificationSound.defaultSound())

                val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier = "local_notif",
                    content = content,
                    trigger = trigger
                )

                center.addNotificationRequest(request) { error ->
                    if (error != null) {
                        println("Notification Error: ${error.localizedDescription}")
                    }
                }
            }
        }
    }
}
