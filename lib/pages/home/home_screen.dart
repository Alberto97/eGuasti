import 'dart:async';

import 'package:eguasti/models/outage.dart';
import 'package:eguasti/models/tracked_outage.dart';
import 'package:eguasti/pages/home/outage_sheet.dart';
import 'package:eguasti/pages/home/home_bloc.dart';
import 'package:eguasti/pages/home/map.dart';
import 'package:eguasti/pages/home/permission_denied_dialog.dart';
import 'package:eguasti/tools/flutter_map_extensions.dart';
import 'package:eguasti/widgets/app_animated_switcher.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_map/plugin_api.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:latlong2/latlong.dart';
import 'package:tuple/tuple.dart';

enum MenuItem { about }

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage>
    with TickerProviderStateMixin, WidgetsBindingObserver {
  late HomeBloc bloc;
  late MapController mapController;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    bloc = HomeBloc();
    mapController = MapController();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    bloc.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      bloc.clearSelectedOutage();
      bloc.fetchOutages();
    }
  }

  Future<bool> shouldPop() async {
    if (bloc.isOutageSelected()) {
      bloc.clearSelectedOutage();
      return false;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () => shouldPop(),
      child: Scaffold(
        appBar: AppBar(
          title: const Text("eGuasti"),
          actions: [
            PopupMenuButton(
              onSelected: (MenuItem item) => handleMenuItemSelection(item),
              itemBuilder: (BuildContext context) => [
                buildAboutItem(),
              ],
            )
          ],
        ),
        body: Stack(
          children: [
            buildSnackBar(),
            buildPermDialog(),
            buildBody(),
          ],
        ),
      ),
    );
  }

  void handleMenuItemSelection(MenuItem item) async {
    if (item == MenuItem.about) {
      await Navigator.of(context).pushNamed("/about");
      bloc.updateTrackingEnabledFeature();
    }
  }

  PopupMenuItem<MenuItem> buildAboutItem() {
    var text = AppLocalizations.of(context)!.mainActionAbout;
    return PopupMenuItem<MenuItem>(value: MenuItem.about, child: Text(text));
  }

  Widget buildSnackBar() {
    return StreamBuilder<bool>(
      stream: bloc.showNotificationChannelSnackBar,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          showPermErrSnackBar();
        }
        return const SizedBox.shrink();
      },
    );
  }

  void showPermErrSnackBar() {
    WidgetsBinding.instance.addPostFrameCallback(
      (_) {
        final message =
            AppLocalizations.of(context)!.notificationChannelCreationFailure;
        final snackBar = SnackBar(
          content: Text(message),
        );
        ScaffoldMessenger.of(context).showSnackBar(snackBar);
      },
    );
  }

  Widget buildPermDialog() {
    return StreamBuilder<bool>(
      stream: bloc.showPermissionDenied,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          WidgetsBinding.instance.addPostFrameCallback((_) {
            showDialog(
              context: context,
              builder: (BuildContext context) {
                return PermissionDeniedDialog(
                  onOkPressed: () => bloc.openSettings(),
                );
              },
            );
          });
        }
        return const SizedBox.shrink();
      },
    );
  }

  Widget buildBody() {
    return FutureBuilder<Tuple2<LatLng, double>>(
      future: bloc.fetchMapState(),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Stack(alignment: AlignmentDirectional.bottomCenter, children: [
            buildMapWithMarkers(
              snapshot.data!.item1,
              snapshot.data!.item2,
            ),
            buildSheetWithTracking()
          ]);
        } else {
          return const SizedBox.shrink();
        }
      },
    );
  }

  Widget buildSheetWithTracking() {
    return StreamBuilder<bool>(
      stream: bloc.outageTrackingEnabled,
      builder: (context, snapshot) {
        final enabled = snapshot.data ?? false;
        return buildSheet(enabled);
      },
    );
  }

  Widget buildSheet(bool trackingFeatureEnabled) {
    return StreamBuilder<TrackedOutage?>(
      stream: bloc.selectedOutageStream,
      builder: (context, snapshot) {
        final outage = snapshot.data;
        return AnimatedSwitcher(
          duration: const Duration(milliseconds: 250),
          transitionBuilder: AppAnimatedSwitcher.slideTransitionBuilder,
          child: outage == null
              ? const SizedBox.shrink()
              : OutageSheet(
                  tracking: outage.tracked,
                  outage: outage.data,
                  track: () => bloc.toggleOutageTracking(),
                  trackingFeatureEnabled: trackingFeatureEnabled,
                ),
        );
      },
    );
  }

  Widget buildMapWithMarkers(LatLng center, double zoom) {
    return StreamBuilder<List<Outage>>(
      stream: bloc.outages,
      builder: (context, snapshot) {
        if (snapshot.hasError) {
          showErrorMessage(snapshot.error! as String);
        }
        if (snapshot.hasData) {
          final markers = snapshot.data!.map((e) => buildMarker(e)).toList();
          return buildMap(context, center, zoom, markers);
        } else {
          return buildMap(context, center, zoom, []);
        }
      },
    );
  }

  void showErrorMessage(String text) {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(text),
          duration: const Duration(days: 1),
          action: SnackBarAction(
            label: AppLocalizations.of(context)!.mainActionTryAgain,
            onPressed: () {
              bloc.fetchOutages();
              ScaffoldMessenger.of(context).removeCurrentSnackBar();
            },
          ),
        ),
      );
    });
  }

  Marker buildMarker(Outage value) {
    final markerPath = value.cause == Cause.failure
        ? 'assets/marker_red.svg'
        : 'assets/marker_yellow.svg';

    return Marker(
      width: 40.0,
      height: 40.0,
      point: LatLng(value.latitude, value.longitude),
      anchorPos: AnchorPos.align(AnchorAlign.top),
      builder: (ctx) => GestureDetector(
        onTap: () => onMarkerTap(value),
        child: SvgPicture.asset(markerPath),
      ),
    );
  }

  void onMarkerTap(Outage value) {
    bloc.setSelectedOutage(value);
    centerToOutage(value);
  }

  void centerToOutage(Outage value) {
    final zoom = mapController.zoom;
    final center = LatLng(value.latitude, value.longitude);
    mapController.animatedMove(this, center, zoom);
  }

  Widget buildMap(
      BuildContext context, LatLng center, double zoom, List<Marker> markers) {
    return OpenStreetMap(
      center: center,
      zoom: zoom,
      mapController: mapController,
      markers: markers,
      onMapTap: (_, __) => bloc.clearSelectedOutage(),
      onPositionChanged: (position, _) => onMapPositionChanged(position),
    );
  }

  void onMapPositionChanged(MapPosition value) {
    if (value.center == null || value.zoom == null) return;
    bloc.saveMapState(value.center!, value.zoom!);
  }
}
