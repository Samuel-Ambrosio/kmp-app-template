import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // TODO: Configure Firebase (FirebaseApp.configure()) after GoogleService-Info.plist is added
        IosAppKt.startIosApp()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
