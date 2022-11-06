import 'package:eguasti/models/app_result.dart';
import 'package:flutter/services.dart';

class NotificationChannel {
  static const platform = MethodChannel('org.alberto97.eguasti/notification');

  Future<AppResult<void>> createNotificationChannel() async {
    try {
      await platform.invokeMethod("createNotificationChannel");
      return AppResult(data: null);
    } on PlatformException {
      return AppResult(message: "Could not create notification channel");
    }
  }
}