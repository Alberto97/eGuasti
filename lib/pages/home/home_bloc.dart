import 'dart:async';

import 'package:flutter_e_guasti/models/app_map_state.dart';
import 'package:flutter_e_guasti/models/outage.dart';
import 'package:flutter_e_guasti/repository/map_state_repository.dart';
import 'package:flutter_e_guasti/repository/outage_repository.dart';
import 'package:latlong2/latlong.dart';
import 'package:tuple/tuple.dart';

class HomeBloc {
  final _outageRepository = OutageRepository();
  final _mapStateRepository = MapStateRepository();

  HomeBloc() {
    fetchOutages();
  }

  final _outageController = StreamController<List<Outage>>();
  get outages => _outageController.stream;

  fetchOutages() async {
    final outages = await _outageRepository.getOutages();
    if (outages.isSuccessful) {
      _outageController.sink.add(outages.data!);
    } else {
      _outageController.sink.addError(outages.message!);
    }
  }

  Future<Tuple2<LatLng, double>> fetchMapState() async {
    final state = await _mapStateRepository.readState();

    // Default values - center on Italy
    var position = LatLng(42.610, 12.041);
    var zoom = 5.0;

    if (state != null) {
      position = LatLng(state.latitude, state.longitude);
      zoom = state.zoom;
    }

    return Tuple2(position, zoom);
  }

  Future<void> saveMapState(LatLng position, double zoom) async {
    final state = AppMapState(position.latitude, position.longitude, zoom);
    await _mapStateRepository.writeState(state);
  }

  void dispose() {
    _outageController.close();
  }
}
