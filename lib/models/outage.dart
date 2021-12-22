enum Cause { failure, maintenance }

extension SqlExtension on Cause {
  String get sql {
    return ["Guasto", "Lavoro Programmato"][index];
  }
}

class Outage {
  int id;
  String start;
  String expectedRestore;
  String lastUpdate;
  String place;
  double latitude;
  double longitude;
  int offlineCustomers;
  Cause cause;

  Outage({
    required this.id,
    required this.start,
    required this.expectedRestore,
    required this.lastUpdate,
    required this.place,
    required this.latitude,
    required this.longitude,
    required this.offlineCustomers,
    required this.cause,
  });
}
