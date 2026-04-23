import SwiftUI
import ComposeApp
import UserNotifications

@main
struct iOSApp: App {
    class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
        func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
            completionHandler([.banner, .sound, .badge])
        }
    }
    
    private let delegate = NotificationDelegate()

    init() {
        KoinIOSKt.startKoinIOS()
        UNUserNotificationCenter.current().delegate = delegate
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}