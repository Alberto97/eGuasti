import 'package:eguasti/tasks/outage_notify_task.dart';
import 'package:eguasti/tools/work_scheduler.dart';

typedef BackgroundTask = Future<bool> Function(Map<String, dynamic>?);

final Map<String, BackgroundTask> backgroundTasks = {
  WorkScheduler.outageNotifyTask: (_) =>
      OutageNotifyTask.init().then((value) => value.start())
};
