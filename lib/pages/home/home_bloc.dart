import 'dart:async';

import 'package:eguasti/models/app_map_state.dart';
import 'package:eguasti/models/outage.dart';
import 'package:eguasti/models/tracked_outage.dart';
import 'package:eguasti/repository/map_state_repository.dart';
import 'package:eguasti/repository/outage_repository.dart';
import 'package:eguasti/repository/outage_tracker.dart';
import 'package:eguasti/tools/notification_channel.dart';
import 'package:eguasti/tools/work_scheduler.dart';
import 'package:latlong2/latlong.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:rxdart/rxdart.dart';
import 'package:tuple/tuple.dart';

class HomeBloc {
  final _outageRepository = OutageRepository();
  final _mapStateRepository = MapStateRepository();
  final _tracker = OutageTracker();
  final _scheduler = WorkScheduler();
  final _notificationChannel = NotificationChannel();
  DateTime? _lastFetch;

  HomeBloc() {
    fetchOutages();
  }

  final _notificationChannelSnackBarController =
      StreamController<bool>.broadcast();
  Stream<bool> get showNotificationChannelSnackBar =>
      _notificationChannelSnackBarController.stream;

  final _permissionDeniedController = StreamController<bool>.broadcast();
  Stream<bool> get showPermissionDenied => _permissionDeniedController.stream;

  final _outageController = StreamController<List<Outage>>();
  Stream<List<Outage>> get outages => _outageController.stream;

  final _selectedOutageController = BehaviorSubject<TrackedOutage?>();
  Stream<TrackedOutage?> get selectedOutageStream =>
      _selectedOutageController.stream;

  fetchOutages() async {
    if (_lastFetch != null) {
      final duration = DateTime.now().difference(_lastFetch!);
      if (duration < const Duration(minutes: 5)) return;
    }
    final outages = await _outageRepository.getOutages();
    if (outages.isSuccessful) {
      _lastFetch = DateTime.now();
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

  void setSelectedOutage(Outage outage) async {
    final tracked = _tracker.isTracking(outage.id);
    final value = TrackedOutage(outage, tracked);
    _selectedOutageController.sink.add(value);
  }

  void clearSelectedOutage() {
    _selectedOutageController.sink.add(null);
  }

  bool isOutageSelected() {
    return _selectedOutageController.value != null;
  }

  void toggleOutageTracking() async {
    if (!await _hasNotificationPerms()) return;

    final outage = _selectedOutageController.value;
    final id = outage!.data.id;
    if (outage.tracked) {
      _tracker.untrack(id);
      _unscheduleIfEmpty();
    } else {
      _tracker.track(id);
      _scheduler.schedule();
    }
    final tracked = _tracker.isTracking(id);
    final newValue = outage.withTracking(tracked);
    _selectedOutageController.sink.add(newValue);
  }

  Future<bool> _hasNotificationPerms() async {
    final status = await Permission.notification.request();
    if (status.isGranted) {
      return await _createNotificationChannel();
    } else {
      clearSelectedOutage();
      _permissionDeniedController.sink.add(true);
    }
    return false;
  }

  Future<bool> _createNotificationChannel() async {
    final result = await _notificationChannel.createNotificationChannel();
    if (!result.isSuccessful) {
      clearSelectedOutage();
      _notificationChannelSnackBarController.add(true);
    }
    return result.isSuccessful;
  }

  void openSettings() async {
    await openAppSettings();
  }

  void _unscheduleIfEmpty() {
    final trackedList = _tracker.getTracked();
    if (trackedList.isEmpty) {
      _scheduler.unschedule();
    }
  }

  void dispose() {
    _outageController.close();
    _selectedOutageController.close();
  }
}
