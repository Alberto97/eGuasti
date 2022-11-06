import 'package:workmanager/workmanager.dart';

class WorkScheduler {
  static const _outageNotifyTaskUniqueName = "outageNotify";
  static const outageNotifyTask = "outageNotify";

  void schedule() {
    Workmanager().registerPeriodicTask(
      _outageNotifyTaskUniqueName,
      outageNotifyTask,
      initialDelay: const Duration(minutes: 15),
      existingWorkPolicy: ExistingWorkPolicy.keep,
      constraints: Constraints(
        networkType: NetworkType.connected,
      ),
    );
  }

  void unschedule() {
    Workmanager().cancelByUniqueName(_outageNotifyTaskUniqueName);
  }
}
