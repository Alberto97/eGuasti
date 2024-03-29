import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:flutter_map_marker_cluster/flutter_map_marker_cluster.dart';
import 'package:latlong2/latlong.dart';

typedef MapClickCallback = void Function(LatLng point);

class OpenStreetMap extends StatefulWidget {
  const OpenStreetMap(
      {Key? key,
      this.center,
      this.zoom,
      this.mapController,
      this.markers = const [],
      this.onMapTap,
      this.onPositionChanged})
      : super(key: key);

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
        center: widget.center,
        zoom: widget.zoom ?? 13.0,
        maxZoom: 18.0,
        interactiveFlags: InteractiveFlag.all & ~InteractiveFlag.rotate,
        onTap: (tapPosition, point) =>
            widget.onMapTap?.call(tapPosition, point),
        onPositionChanged: (position, hasGesture) =>
            widget.onPositionChanged?.call(position, hasGesture),
      ),
      nonRotatedChildren: [buildMapAttribution()],
      children: [
        buildOsmTileLayer(),
        buildMarkerClusterLayer(widget.markers),
      ],
    );
  }

  TileLayer buildOsmTileLayer() {
    return TileLayer(
      urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      maxNativeZoom: 18,
      subdomains: const ['a', 'b', 'c'],
    );
  }

  Widget buildMapAttribution() {
    return Align(
      alignment: Alignment.bottomRight,
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.5),
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(5.0),
          ),
        ),
        child: const Padding(
          padding: EdgeInsets.all(3.0),
          child: Text(
            "© OpenStreetMap contributors",
            style: TextStyle(fontSize: 12.0),
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
        fitBoundsOptions: const FitBoundsOptions(
          padding: EdgeInsets.all(50),
        ),
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
