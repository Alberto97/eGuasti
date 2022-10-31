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

  factory Outage.fromAttributes(Map<String, dynamic> map) => Outage(
        id: map['id_interruzione'] as int,
        start: map['data_interruzione'] as String,
        expectedRestore: map['data_prev_ripristino'] as String,
        lastUpdate: map['dataultimoaggiornamento'] as String,
        place: map['descrizione_territoriale'] as String,
        latitude: (map['latitudine'] as num).toDouble(),
        longitude: (map['longitudine'] as num).toDouble(),
        offlineCustomers: map['num_cli_disalim'] as int,
        cause: map['causa_disalimentazione'] == "Guasto"
            ? Cause.failure
            : Cause.maintenance,
      );
}
