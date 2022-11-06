import 'package:eguasti/models/outage.dart';
import 'package:eguasti/repository/outage_repository.dart';
import 'package:eguasti/repository/outage_tracker.dart';
import 'package:eguasti/tools/outage_list_helper.dart';
import 'package:eguasti/tools/android_intent_helper.dart';
import 'package:eguasti/tools/work_scheduler.dart';

class OutageNotifyTask {
  late OutageRepository _repository;
  late OutageTracker _tracker;
  late WorkScheduler _scheduler;
  late AndroidIntentHelper _intentHelper;

  OutageNotifyTask({
    OutageRepository? repository,
    OutageTracker? tracker,
    WorkScheduler? scheduler,
    AndroidIntentHelper? intentHelper,
  }) {
    _repository = repository ?? OutageRepository();
    _tracker = tracker ?? OutageTracker();
    _scheduler = scheduler ?? WorkScheduler();
    _intentHelper = intentHelper ?? AndroidIntentHelper();
  }

  Future<bool> start() async {
    final result = await _repository.getOutages();
    if (result.isSuccessful) {
      _processOutages(result.data!);
      return Future.value(true);
    } else {
      return Future.value(false);
    }
  }

  void _processOutages(List<Outage> outages) {
    final tracked = _tracker.getTracked();
    final ongoingTracked = OutageListHelper.findTracked(tracked, outages);
    _notify(ongoingTracked);
    _untrackResolved(tracked, ongoingTracked);
    _unscheduleIfEmpty(ongoingTracked);
  }

  void _notify(List<Outage> ongoingTracked) {
    ongoingTracked.forEach(_intentHelper.notifyOutage);
  }

  void _untrackResolved(List<int> tracked, List<Outage> ongoingTracked) {
    final toUntrack =
        OutageListHelper.findTrackedResolvedIds(tracked, ongoingTracked);
    toUntrack.forEach(_tracker.untrack);
  }

  void _unscheduleIfEmpty(List<Outage> ongoingTracked) {
    if (ongoingTracked.isEmpty) {
      _scheduler.unschedule();
    }
  }
}
