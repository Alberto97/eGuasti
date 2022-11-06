package org.alberto97.eguasti

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val notificationMethodChannel = "org.alberto97.eguasti/notification"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        configureFlutterEngine(flutterEngine.dartExecutor.binaryMessenger)
    }

    private fun configureFlutterEngine(messenger: BinaryMessenger) {
        val methodChannel = MethodChannel(messenger, notificationMethodChannel)
        methodChannel.setMethodCallHandler(NotificationCallHandler(this.applicationContext))
    }

}
