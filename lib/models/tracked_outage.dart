import 'package:eguasti/models/outage.dart';

class TrackedOutage {
  final Outage data;
  final bool tracked;
  const TrackedOutage(this.data, this.tracked);
  TrackedOutage withTracking(bool value) => TrackedOutage(data, value);
}
