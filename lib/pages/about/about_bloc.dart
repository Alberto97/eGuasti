import 'dart:async';

import 'package:package_info_plus/package_info_plus.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutBloc {
  late PackageInfo packageInfo;

  AboutBloc() {
    _initPackageInfo();
  }

  _initPackageInfo() async {
    packageInfo = await PackageInfo.fromPlatform();
    _initAppVersion();
  }

  _initAppVersion() {
    String version = packageInfo.version;
    String buildNumber = packageInfo.buildNumber;
    var versionString = "$version ($buildNumber)";
    _appVersionController.sink.add(versionString);
  }

  final _appVersionController = StreamController<String>();
  Stream<String> get appVersion => _appVersionController.stream;

  void openRepository() {
    final uri = Uri.parse("https://github.com/Alberto97/eGuasti");
    launchUrl(uri, mode: LaunchMode.externalApplication);
  }

  void dispose() {
    _appVersionController.close();
  }
}
