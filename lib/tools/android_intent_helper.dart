import 'dart:io';

import 'package:android_intent_plus/android_intent.dart';

class AndroidIntentHelper {
  
  Future<void> notifyOutage(int id, String title, String text) async {
    if (!Platform.isAndroid) return;

    final data = {
      'title': title,
      'text': text,
      'id': id,
    };
    final intent = AndroidIntent(
      action: 'org.alberto97.eguasti.SEND_NOTIFICATION',
      package: 'org.alberto97.eguasti',
      componentName: 'org.alberto97.eguasti.NotificationReceiver',
      arguments: data,
    );
    await intent.sendBroadcast();
  }
}