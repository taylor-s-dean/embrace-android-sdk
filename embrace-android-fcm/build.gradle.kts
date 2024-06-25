plugins {
    id("internal-embrace-plugin")
}

description = "Embrace Android SDK: Firebase Cloud Messaging"

android {
    namespace = "io.embrace.android.embracesdk.fcm"
}

dependencies {
    compileOnly(libs.firebase.messaging)
    compileOnly(project(":embrace-android-sdk"))
}
