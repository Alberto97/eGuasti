import 'dart:async';

import 'package:eguasti/models/outage.dart';
import 'package:eguasti/pages/home/outage_sheet.dart';
import 'package:eguasti/pages/home/home_cubit.dart';
import 'package:eguasti/pages/home/map.dart';
import 'package:eguasti/pages/home/permission_denied_dialog.dart';
import 'package:eguasti/tools/flutter_map_extensions.dart';
import 'package:eguasti/widgets/app_animated_switcher.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:flutter_map/plugin_api.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:latlong2/latlong.dart';
import 'package:tuple/tuple.dart';

enum MenuItem { about }

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => HomeCubit(),
      child: const _HomePage(),
    );
  }
}

class _HomePage extends StatefulWidget {
  const _HomePage({Key? key}) : super(key: key);

  @override
  State<_HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<_HomePage>
    with TickerProviderStateMixin, WidgetsBindingObserver {
  late MapController mapController;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    mapController = MapController();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      context.read<HomeCubit>().clearSelectedOutage();
      context.read<HomeCubit>().fetchOutages();
    }
  }

  Future<bool> shouldPop() async {
    if (context.read<HomeCubit>().isOutageSelected()) {
      context.read<HomeCubit>().clearSelectedOutage();
      return false;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    final cubit = context.read<HomeCubit>();
    return WillPopScope(
      onWillPop: () => shouldPop(),
      child: Scaffold(
        appBar: AppBar(
          title: const Text("eGuasti"),
          actions: [
            PopupMenuButton(
              onSelected: (MenuItem item) =>
                  handleMenuItemSelection(cubit, item),
              itemBuilder: (BuildContext context) => [
                buildAboutItem(),
              ],
            )
          ],
        ),
        body: MultiBlocListener(
          listeners: [
            BlocListener<HomeCubit, HomeState>(
              listenWhen: (previous, current) =>
                  current.snackBarMessage != HomeSnackBarMessage.none,
              listener: (context, state) => showSnackBar(state.snackBarMessage),
            ),
            BlocListener<HomeCubit, HomeState>(
              listenWhen: (previous, current) =>
                  current.showPermissionDenied == true,
              listener: (context, state) => showPermissionDeniedDialog(),
            )
          ],
          child: buildBody(),
        ),
      ),
    );
  }

  void handleMenuItemSelection(HomeCubit cubit, MenuItem item) async {
    if (item == MenuItem.about) {
      await Navigator.of(context).pushNamed("/about");
      cubit.updateTrackingEnabledFeature();
    }
  }

  PopupMenuItem<MenuItem> buildAboutItem() {
    var text = AppLocalizations.of(context)!.mainActionAbout;
    return PopupMenuItem<MenuItem>(value: MenuItem.about, child: Text(text));
  }

  void showSnackBar(HomeSnackBarMessage message) {
    switch (message) {
      case HomeSnackBarMessage.fetchDataFailure:
        showFetchFailureSnackBar();
        break;
      case HomeSnackBarMessage.notificationChannelFailure:
        showNotificationChannelFailureSnackBar();
        break;
      case HomeSnackBarMessage.none:
        // no-op
        break;
    }
    context.read<HomeCubit>().hideSnackBar();
  }

  void showNotificationChannelFailureSnackBar() {
    final localizations = AppLocalizations.of(context);
    final message = localizations!.notificationChannelCreationFailure;
    final snackBar = SnackBar(
      content: Text(message),
    );
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(snackBar);
  }

  void showFetchFailureSnackBar() {
    final localizations = AppLocalizations.of(context);
    final snackBar = SnackBar(
      content: Text(localizations!.outageFetchFailure),
      duration: const Duration(days: 1),
      action: SnackBarAction(
        label: AppLocalizations.of(context)!.mainActionTryAgain,
        onPressed: () {
          context.read<HomeCubit>().fetchOutages();
          ScaffoldMessenger.of(context).removeCurrentSnackBar();
        },
      ),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  void showPermissionDeniedDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return PermissionDeniedDialog(
          onOkPressed: () => context.read<HomeCubit>().openSettings(),
        );
      },
    );
    context.read<HomeCubit>().hidePermissionDenied();
  }

  Widget buildBody() {
    return FutureBuilder<Tuple2<LatLng, double>>(
      future: context.read<HomeCubit>().fetchMapState(),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return Stack(alignment: AlignmentDirectional.bottomCenter, children: [
            buildMapWithMarkers(
              snapshot.data!.item1,
              snapshot.data!.item2,
            ),
            buildSheet()
          ]);
        } else {
          return const SizedBox.shrink();
        }
      },
    );
  }

  Widget buildSheet() {
    return BlocBuilder<HomeCubit, HomeState>(
      buildWhen: (previous, current) =>
          previous.selectedOutage != current.selectedOutage ||
          previous.outageTrackingEnabled != current.outageTrackingEnabled,
      builder: (context, state) {
        return AnimatedSwitcher(
          duration: const Duration(milliseconds: 250),
          transitionBuilder: AppAnimatedSwitcher.slideTransitionBuilder,
          child: state.selectedOutage == null
              ? const SizedBox.shrink()
              : OutageSheet(
                  tracking: state.selectedOutage!.tracked,
                  outage: state.selectedOutage!.data,
                  track: () => context.read<HomeCubit>().toggleOutageTracking(),
                  trackingFeatureEnabled: state.outageTrackingEnabled,
                ),
        );
      },
    );
  }

  Widget buildMapWithMarkers(LatLng center, double zoom) {
    return BlocBuilder<HomeCubit, HomeState>(
      buildWhen: (previous, current) => previous.outages != current.outages,
      builder: (context, state) {
        final markers = state.outages.map((e) => buildMarker(e)).toList();
        return OpenStreetMap(
          center: center,
          zoom: zoom,
          mapController: mapController,
          markers: markers,
          onMapTap: (_, __) => context.read<HomeCubit>().clearSelectedOutage(),
          onPositionChanged: (position, _) => onMapPositionChanged(position),
        );
      },
    );
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
    context.read<HomeCubit>().setSelectedOutage(value);
    centerToOutage(value);
  }

  void centerToOutage(Outage value) {
    final zoom = mapController.zoom;
    final center = LatLng(value.latitude, value.longitude);
    mapController.animatedMove(this, center, zoom);
  }

  void onMapPositionChanged(MapPosition value) {
    if (value.center == null || value.zoom == null) return;
    context.read<HomeCubit>().saveMapState(value.center!, value.zoom!);
  }
}
