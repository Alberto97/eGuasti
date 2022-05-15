import 'package:flutter/material.dart';

class AppAnimatedSwitcher {
  static Widget slideTransitionBuilder(
      Widget child, Animation<double> animation) {
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
  }
}
