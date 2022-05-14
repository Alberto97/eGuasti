import 'package:eguasti/models/outage.dart';
import 'package:eguasti/pages/home/outage_sheet.dart';
import 'package:eguasti/pages/home/home_bloc.dart';
import 'package:eguasti/pages/home/map.dart';
import 'package:eguasti/tools/flutter_map_extensions.dart';
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

  Outage? selectedOutage;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addObserver(this);
    bloc = HomeBloc();
    mapController = MapController();
  }

  @override
  void dispose() {
    WidgetsBinding.instance?.removeObserver(this);
    bloc.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      clearSelectedOutage();
      bloc.fetchOutages();
    }
  }

  Future<bool> shouldPop() async {
    if (selectedOutage != null) {
      clearSelectedOutage();
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
        body: buildBody(),
      ),
    );
  }

  void handleMenuItemSelection(MenuItem item) {
    if (item == MenuItem.about) Navigator.of(context).pushNamed("/about");
  }

  PopupMenuItem<MenuItem> buildAboutItem() {
    var text = AppLocalizations.of(context)!.mainActionAbout;
    return PopupMenuItem<MenuItem>(value: MenuItem.about, child: Text(text));
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
            buildSheet()
          ]);
        } else {
          return const SizedBox.shrink();
        }
      },
    );
  }

  Widget buildSheet() {
    return AnimatedSwitcher(
      duration: const Duration(milliseconds: 250),
      transitionBuilder: (Widget child, Animation<double> animation) {
        final offsetAnimation = Tween(
          begin: const Offset(0.0, 1.0),
          end: Offset.zero,
        ).animate(animation);
        return ClipRect(
          child: SlideTransition(
            position: offsetAnimation,
            child: child,
          ),
        );
      },
      child: selectedOutage == null
          ? const SizedBox.shrink()
          : OutageSheet(outage: selectedOutage!),
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
    WidgetsBinding.instance!.addPostFrameCallback((_) {
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
    setState(() {
      selectedOutage = value;
    });
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
      onMapTap: (_, __) => clearSelectedOutage(),
      onPositionChanged: (position, _) => onMapPositionChanged(position),
    );
  }

  void clearSelectedOutage() {
    setState(() {
      selectedOutage = null;
    });
  }

  void onMapPositionChanged(MapPosition value) {
    if (value.center == null || value.zoom == null) return;
    bloc.saveMapState(value.center!, value.zoom!);
  }
}
