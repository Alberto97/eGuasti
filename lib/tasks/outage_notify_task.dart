import 'dart:ui';

import 'package:eguasti/l10n/app_localizations.dart';
import 'package:eguasti/models/outage.dart';
import 'package:eguasti/repository/outage_repository.dart';
import 'package:eguasti/repository/outage_tracker.dart';
import 'package:eguasti/tools/outage_list_helper.dart';
import 'package:eguasti/tools/android_intent_helper.dart';
import 'package:eguasti/tools/work_scheduler.dart';
import 'package:intl/intl.dart';

class OutageNotifyTask {
  final OutageRepository _repository;
  final OutageTracker _tracker;
  final WorkScheduler _scheduler;
  final AndroidIntentHelper _intentHelper;
  final AppLocalizations _localizations;

  OutageNotifyTask._(
    this._repository,
    this._tracker,
    this._scheduler,
    this._intentHelper,
    this._localizations,
  );

  static Future<OutageNotifyTask> init({
    OutageRepository? outageRepository,
    OutageTracker? outageTracker,
    WorkScheduler? workScheduler,
    AndroidIntentHelper? androidIntentHelper,
  }) async {
    final repository = outageRepository ?? OutageRepository();
    final tracker = outageTracker ?? OutageTracker();
    final scheduler = workScheduler ?? WorkScheduler();
    final intentHelper = androidIntentHelper ?? AndroidIntentHelper();

    final currentLocaleStr = Intl.getCurrentLocale();
    final currentLocale = Locale(currentLocaleStr.split("_")[0]);
    final localizations = await AppLocalizations.delegate.load(currentLocale);

    return OutageNotifyTask._(
      repository,
      tracker,
      scheduler,
      intentHelper,
      localizations,
    );
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
    for (var outage in ongoingTracked) {
      final expectedRestore = outage.expectedRestore;
      final text =_localizations.trackOutageNotificationMessage(expectedRestore);
      _intentHelper.notifyOutage(outage.id, outage.place, text);
    }
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
