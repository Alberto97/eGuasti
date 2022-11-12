import 'dart:async';

import 'package:eguasti/data/preference_manager.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutBloc {
  late PackageInfo _packageInfo;
  late PreferenceManager _preferenceManager;

  AboutBloc() {
    _init();
  }

  _init() async {
    _preferenceManager = await PreferenceManager.init();
    _packageInfo = await PackageInfo.fromPlatform();
    _initTrackOutagesEnabled();
    _initAppVersion();
  }

  final _appVersionController = StreamController<String>();
  Stream<String> get appVersion => _appVersionController.stream;

  final _trackOutagesEnabled = StreamController<bool>();
  Stream<bool> get trackOutagesEnabled => _trackOutagesEnabled.stream;

  void _initTrackOutagesEnabled() async {
    final enabled = await _preferenceManager.isOutageTrackingEnabled();
    _trackOutagesEnabled.sink.add(enabled);
  }

  void setTrackOutagesEnabled(bool value) async {
    await _preferenceManager.setOutageTrackingEnabled(value);
    _trackOutagesEnabled.sink.add(value);
  }

  void _initAppVersion() {
    String version = _packageInfo.version;
    String buildNumber = _packageInfo.buildNumber;
    var versionString = "$version ($buildNumber)";
    _appVersionController.sink.add(versionString);
  }

  void openRepository() {
    final uri = Uri.parse("https://github.com/Alberto97/eGuasti");
    launchUrl(uri, mode: LaunchMode.externalApplication);
  }

  void dispose() {
    _appVersionController.close();
  }
}
