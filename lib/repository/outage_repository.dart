import 'dart:io';

import 'package:chopper/chopper.dart';
import 'package:eguasti/models/app_result.dart';
import 'package:eguasti/models/outage.dart';
import 'package:eguasti/models/response.dart';
import 'package:eguasti/network/api_service.dart';
import 'package:eguasti/tools/settings.dart';

class OutageRepository {
  late ApiService apiService;

  OutageRepository() {
    _initializeClient();
  }

  void _initializeClient() {
    const converter = JsonSerializableConverter({
      FeatureCollection: FeatureCollection.fromJson,
    });

    final chopper = ChopperClient(
      converter: converter,
      baseUrl: Settings.endpointUrl,
      services: [ApiService.create()],
    );
    apiService = ApiService.create(chopper);
  }

  Future<AppResult<List<Outage>>> getOutages() async {
    final failures = await _getFailures();
    if (!failures.isSuccessful) return failures;

    final maintenance = await _getMaintenance();
    if (!maintenance.isSuccessful) return maintenance;

    return AppResult(data: maintenance.data! + failures.data!);
  }

  Future<AppResult<List<Outage>>> _getFailures() async {
    return await _getOutages(Cause.failure);
  }

  Future<AppResult<List<Outage>>> _getMaintenance() async {
    return await _getOutages(Cause.maintenance);
  }

  Future<AppResult<List<Outage>>> _getOutages(Cause cause) async {
    // final where =
    //     "CAUSA_DISALIMENTAZIONE='${cause.sql}' and Regione = 'Veneto'";
    final where = "CAUSA_DISALIMENTAZIONE='${cause.sql}'";
    try {
      final response = await apiService.query(where);
      if (response.isSuccessful) {
        final list = response.body!.features
            .map((item) => _mapOutage(item.attributes, cause))
            .toList();
        return AppResult(data: list);
      } else {
        return AppResult(message: "An error occurred while retrieving outages");
      }
    } on IOException catch (_) {
      return AppResult(message: "Please check your connectivity and try again");
    }
  }

  Outage _mapOutage(Attributes data, Cause cause) {
    return Outage(
      id: data.idInterruzione,
      start: data.dataInterruzione,
      expectedRestore: data.dataPrevRipristino,
      lastUpdate: data.dataUltimoAggiornamento,
      place: data.descrizioneTerritoriale,
      latitude: data.latitudine,
      longitude: data.longitudine,
      offlineCustomers: data.numCliDisalim,
      cause: cause,
    );
  }
}
