import 'dart:async';

import 'package:eguasti/data/preference_manager.dart';
import 'package:eguasti/models/app_map_state.dart';
import 'package:eguasti/models/outage.dart';
import 'package:eguasti/models/tracked_outage.dart';
import 'package:eguasti/repository/map_state_repository.dart';
import 'package:eguasti/repository/outage_repository.dart';
import 'package:eguasti/repository/outage_tracker.dart';
import 'package:eguasti/tools/notification_channel.dart';
import 'package:eguasti/tools/work_scheduler.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:latlong2/latlong.dart';
import 'package:permission_handler/permission_handler.dart';

enum HomeSnackBarMessage {
  none,
  notificationChannelFailure,
  fetchDataFailure,
}

class HomeState extends Equatable {
  final bool outageTrackingEnabled;
  final bool showPermissionDenied;
  final HomeSnackBarMessage snackBarMessage;
  final List<Outage> outages;
  final TrackedOutage? selectedOutage;

  const HomeState({
    this.outageTrackingEnabled = false,
    this.showPermissionDenied = false,
    this.snackBarMessage = HomeSnackBarMessage.none,
    this.outages = const [],
    this.selectedOutage,
  });

  @override
  List<Object?> get props => [
        outageTrackingEnabled,
        showPermissionDenied,
        snackBarMessage,
        outages,
        selectedOutage,
      ];

  HomeState copyWith({
    bool? outageTrackingEnabled,
    bool? showPermissionDenied,
    HomeSnackBarMessage? snackBarMessage,
    List<Outage>? outages,
    TrackedOutage? selectedOutage,
  }) {
    return HomeState(
      outageTrackingEnabled:
          outageTrackingEnabled ?? this.outageTrackingEnabled,
      showPermissionDenied: showPermissionDenied ?? this.showPermissionDenied,
      snackBarMessage: snackBarMessage ?? this.snackBarMessage,
      outages: outages ?? this.outages,
      selectedOutage: selectedOutage ?? this.selectedOutage,
    );
  }

  HomeState copyWithUnselectedOutage() {
    return HomeState(
      outageTrackingEnabled: outageTrackingEnabled,
      showPermissionDenied: showPermissionDenied,
      snackBarMessage: snackBarMessage,
      outages: outages,
      selectedOutage: null,
    );
  }
}

class HomeCubit extends Cubit<HomeState> {
  final _outageRepository = OutageRepository();
  final _mapStateRepository = MapStateRepository();
  final _tracker = OutageTracker();
  final _scheduler = WorkScheduler();
  final _notificationChannel = NotificationChannel();
  late PreferenceManager _preferenceManager;
  DateTime? _lastFetch;

  HomeCubit() : super(const HomeState()) {
    fetchOutages();
    _init();
  }

  _init() async {
    _preferenceManager = await PreferenceManager.init();
    updateTrackingEnabledFeature();
  }

  fetchOutages() async {
    if (_lastFetch != null) {
      final duration = DateTime.now().difference(_lastFetch!);
      if (duration < const Duration(minutes: 5)) return;
    }
    final outages = await _outageRepository.getOutages();
    if (outages.isSuccessful) {
      _lastFetch = DateTime.now();
      emit(state.copyWith(outages: outages.data!));
    } else {
      emit(state.copyWith(snackBarMessage: HomeSnackBarMessage.fetchDataFailure));
    }
  }

  Future<(LatLng, double)> fetchMapState() async {
    final state = await _mapStateRepository.readState();

    // Default values - center on Italy
    var position = const LatLng(42.610, 12.041);
    var zoom = 5.0;

    if (state != null) {
      position = LatLng(state.latitude, state.longitude);
      zoom = state.zoom;
    }

    return (position, zoom);
  }

  Future<void> saveMapState(LatLng position, double zoom) async {
    final state = AppMapState(position.latitude, position.longitude, zoom);
    await _mapStateRepository.writeState(state);
  }

  void setSelectedOutage(Outage outage) async {
    final tracked = _tracker.isTracking(outage.id);
    final value = TrackedOutage(outage, tracked);
    emit(state.copyWith(selectedOutage: value));
  }

  void clearSelectedOutage() {
    emit(state.copyWithUnselectedOutage());
  }

  bool isOutageSelected() {
    return state.selectedOutage != null;
  }

  void toggleOutageTracking() async {
    if (!await _hasNotificationPerms()) return;

    final outage = state.selectedOutage;
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
    emit(state.copyWith(selectedOutage: newValue));
  }

  Future<bool> _hasNotificationPerms() async {
    final status = await Permission.notification.request();
    if (status.isGranted) {
      return await _createNotificationChannel();
    } else {
      clearSelectedOutage();
      emit(state.copyWith(showPermissionDenied: true));
    }
    return false;
  }

  Future<bool> _createNotificationChannel() async {
    final result = await _notificationChannel.createNotificationChannel();
    if (!result.isSuccessful) {
      clearSelectedOutage();
      emit(state.copyWith(snackBarMessage: HomeSnackBarMessage.notificationChannelFailure));
    }
    return result.isSuccessful;
  }

  void openSettings() async {
    await openAppSettings();
  }

  void hideSnackBar() {
    emit(state.copyWith(snackBarMessage: HomeSnackBarMessage.none));
  }

  void hidePermissionDenied() {
    emit(state.copyWith(showPermissionDenied: false));
  }

  void _unscheduleIfEmpty() {
    final trackedList = _tracker.getTracked();
    if (trackedList.isEmpty) {
      _scheduler.unschedule();
    }
  }

  void updateTrackingEnabledFeature() async {
    final trackingEnabled = await _preferenceManager.isOutageTrackingEnabled();
    emit(state.copyWith(outageTrackingEnabled: trackingEnabled));
  }
}
