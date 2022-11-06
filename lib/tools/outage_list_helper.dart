import 'package:dartx/dartx.dart';
import 'package:eguasti/models/outage.dart';

class OutageListHelper {

  static List<Outage> findTracked(List<int> trackedIds, List<Outage> outages) {
    return trackedIds
        .mapNotNull((trackedId) => _findTrackedOutage(trackedId, outages))
        .toList();
  }

  static Outage? _findTrackedOutage(int trackedId, List<Outage> outages) {
    try {
      final outage = outages.firstWhere((outage) => outage.id == trackedId);
      return outage;
    } on StateError {
      return null;
    }
  }

  static List<int> findTrackedResolvedIds(List<int> trackedIds, List<Outage> ongoingOutages) {
    final ongoingIds = ongoingOutages.map((e) => e.id);
    final toUntrack = trackedIds.where((id) => !ongoingIds.contains(id));
    return toUntrack.toList();
  }
}
