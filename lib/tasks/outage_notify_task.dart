import 'dart:ui';

import 'package:eguasti/models/outage.dart';
import 'package:eguasti/repository/outage_repository.dart';
import 'package:eguasti/repository/outage_tracker.dart';
import 'package:eguasti/tools/outage_list_helper.dart';
import 'package:eguasti/tools/android_intent_helper.dart';
import 'package:eguasti/tools/work_scheduler.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
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
    OutageRepository? repository,
    OutageTracker? tracker,
    WorkScheduler? scheduler,
    AndroidIntentHelper? intentHelper,
  }) async {
    final _repository = repository ?? OutageRepository();
    final _tracker = tracker ?? OutageTracker();
    final _scheduler = scheduler ?? WorkScheduler();
    final _intentHelper = intentHelper ?? AndroidIntentHelper();

    final currentLocaleStr = Intl.getCurrentLocale();
    final currentLocale = Locale(currentLocaleStr.split("_")[0]);
    final localizations = await AppLocalizations.delegate.load(currentLocale);

    return OutageNotifyTask._(
      _repository,
      _tracker,
      _scheduler,
      _intentHelper,
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
