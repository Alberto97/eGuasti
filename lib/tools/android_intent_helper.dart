import 'dart:io';

import 'package:android_intent_plus/android_intent.dart';
import 'package:eguasti/models/outage.dart';

class AndroidIntentHelper {
  
  Future<void> notifyOutage(Outage outage) async {
    if (!Platform.isAndroid) return;

    final data = {
      'title': outage.place,
      'text': outage.expectedRestore,
      'id': outage.id,
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