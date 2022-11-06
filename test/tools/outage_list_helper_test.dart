import 'package:eguasti/models/outage.dart';
import 'package:eguasti/tools/outage_list_helper.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  Outage createOutage(int id) {
    return Outage(
      id: id,
      start: "",
      expectedRestore: "",
      lastUpdate: "",
      place: "",
      latitude: 0.0,
      longitude: 0.0,
      offlineCustomers: 10,
      cause: Cause.failure,
    );
  }

  test("Returned outages must match the respective tracked ones", () {
    final tracked = [1, 3, 5];
    final outages = [
      createOutage(1),
      createOutage(2),
      createOutage(3),
      createOutage(4),
      createOutage(5),
    ];

    final result = OutageListHelper.findTracked(tracked, outages);

    expect(result.length, tracked.length);
    expect(result[0].id, tracked[0]);
    expect(result[1].id, tracked[1]);
    expect(result[2].id, tracked[2]);
  });

  test("Return missing ids from input outages", () {
    final trackedIds = [1, 2, 3, 4, 5];
    final ongoingOutages = [
      createOutage(2),
      createOutage(5),
    ];

    final result = OutageListHelper.findTrackedResolvedIds(trackedIds, ongoingOutages);

    expect(result[0], 1);
    expect(result[1], 3);
    expect(result[2], 4);
  });
}
