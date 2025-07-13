import 'package:eguasti/data/preference_manager.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutState extends Equatable {
  final String appVersion;
  final bool trackOutagesEnabled;

  const AboutState({this.appVersion = "", this.trackOutagesEnabled = false});

  @override
  List<Object?> get props => [appVersion, trackOutagesEnabled];

  AboutState copyWith({String? appVersion, bool? trackOutagesEnabled}) {
    return AboutState(
      appVersion: appVersion ?? this.appVersion,
      trackOutagesEnabled: trackOutagesEnabled ?? this.trackOutagesEnabled,
    );
  }
}

class AboutCubit extends Cubit<AboutState> {
  late PackageInfo _packageInfo;
  late PreferenceManager _preferenceManager;

  AboutCubit() : super(const AboutState()) {
    _init();
  }

  void _init() async {
    _preferenceManager = await PreferenceManager.init();
    _packageInfo = await PackageInfo.fromPlatform();
    _initTrackOutagesEnabled();
    _initAppVersion();
  }

  void _initTrackOutagesEnabled() async {
    final enabled = await _preferenceManager.isOutageTrackingEnabled();
    emit(state.copyWith(trackOutagesEnabled: enabled));
  }

  void setTrackOutagesEnabled(bool value) async {
    await _preferenceManager.setOutageTrackingEnabled(value);
    emit(state.copyWith(trackOutagesEnabled: value));
  }

  void _initAppVersion() {
    String version = _packageInfo.version;
    String buildNumber = _packageInfo.buildNumber;
    var versionString = "$version ($buildNumber)";
    emit(state.copyWith(appVersion: versionString));
  }

  void openRepository() {
    final uri = Uri.parse("https://github.com/Alberto97/eGuasti");
    launchUrl(uri, mode: LaunchMode.externalApplication);
  }
}
