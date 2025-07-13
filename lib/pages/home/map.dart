import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:flutter_map_marker_cluster/flutter_map_marker_cluster.dart';
import 'package:latlong2/latlong.dart';

typedef MapClickCallback = void Function(LatLng point);

class OpenStreetMap extends StatefulWidget {
  const OpenStreetMap(
      {super.key,
      this.center,
      this.zoom,
      this.mapController,
      this.markers = const [],
      this.onMapTap,
      this.onPositionChanged});

  final LatLng? center;
  final double? zoom;
  final TapCallback? onMapTap;
  final PositionCallback? onPositionChanged;
  final MapController? mapController;
  final List<Marker> markers;

  @override
  State<OpenStreetMap> createState() => _OpenStreetMapState();
}

class _OpenStreetMapState extends State<OpenStreetMap> {
  @override
  Widget build(BuildContext context) {
    return FlutterMap(
      key: widget.key,
      mapController: widget.mapController,
      options: MapOptions(
        initialCenter: widget.center ?? const LatLng(0.0, 0.0),
        initialZoom: widget.zoom ?? 13.0,
        maxZoom: 18.0,
        interactionOptions: const InteractionOptions(
          flags: InteractiveFlag.all & ~InteractiveFlag.rotate,
        ),
        onTap: (tapPosition, point) =>
            widget.onMapTap?.call(tapPosition, point),
        onPositionChanged: (position, hasGesture) =>
            widget.onPositionChanged?.call(position, hasGesture),
      ),
      children: [
        buildOsmTileLayer(),
        buildMarkerClusterLayer(widget.markers),
        buildMapAttribution()
      ],
    );
  }

  TileLayer buildOsmTileLayer() {
    return TileLayer(
      urlTemplate: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
      maxNativeZoom: 18,
      subdomains: const ['a', 'b', 'c'],
      userAgentPackageName: "org.alberto97.eguasti",
    );
  }

  Widget buildMapAttribution() {
    return Align(
      alignment: Alignment.bottomRight,
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: Colors.white.withAlpha(128),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(5.0),
          ),
        ),
        child: const Padding(
          padding: EdgeInsets.all(3.0),
          child: TextSourceAttribution(
            "OpenStreetMap contributors",
            textStyle: TextStyle(fontSize: 12.0),
          ),
        ),
      ),
    );
  }

  MarkerLayer buildMarkerLayer(List<Marker> markers) {
    return MarkerLayer(
      markers: markers,
    );
  }

  String getHeroTag(List<Marker> markers) {
    final ids = markers.map((e) => "${e.point.latitude}-${e.point.longitude}");
    return ids.join();
  }

  MarkerClusterLayerWidget buildMarkerClusterLayer(List<Marker> markers) {
    return MarkerClusterLayerWidget(
      options: MarkerClusterLayerOptions(
        maxClusterRadius: 120,
        size: const Size(40, 40),
        markers: markers,
        polygonOptions: const PolygonOptions(
          borderColor: Colors.blueAccent,
          color: Colors.transparent,
          borderStrokeWidth: 3,
        ),
        builder: (context, markers) {
          return FloatingActionButton(
            heroTag: getHeroTag(markers),
            onPressed: null,
            child: Text(markers.length.toString()),
          );
        },
      ),
    );
  }
}
