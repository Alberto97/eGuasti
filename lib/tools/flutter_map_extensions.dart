import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';

extension AnimatedMapMove on MapController {
  // From https://github.com/fleaflet/flutter_map/blob/master/example/lib/pages/animated_map_controller.dart
  void animatedMove(
      TickerProvider tickerProvider, LatLng destLocation, double destZoom) {
    // Create some tweens. These serve to split up the transition from one location to another.
    // In our case, we want to split the transition be<tween> our current map center and the destination.
    final _latTween =
        Tween<double>(begin: center.latitude, end: destLocation.latitude);
    final _lngTween =
        Tween<double>(begin: center.longitude, end: destLocation.longitude);
    final _zoomTween = Tween<double>(begin: zoom, end: destZoom);

    // Create a animation controller that has a duration and a TickerProvider.
    var controller = AnimationController(
      duration: const Duration(milliseconds: 500),
      vsync: tickerProvider,
    );

    // The animation determines what path the animation will take. You can try different Curves values, although I found
    // fastOutSlowIn to be my favorite.
    Animation<double> animation =
        CurvedAnimation(parent: controller, curve: Curves.fastOutSlowIn);

    controller.addListener(() {
      move(LatLng(_latTween.evaluate(animation), _lngTween.evaluate(animation)),
          _zoomTween.evaluate(animation));
    });

    animation.addStatusListener((status) {
      if (status == AnimationStatus.completed ||
          status == AnimationStatus.dismissed) {
        controller.dispose();
      }
    });

    controller.forward();
  }
}
