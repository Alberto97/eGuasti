import 'package:shared_preferences/shared_preferences.dart';

const _kOutageTrackingEnabled = "outage_tracking_enabled";

class PreferenceManager {
  late SharedPreferences preferences;

  PreferenceManager._(this.preferences);

  static Future<PreferenceManager> init() async {
    final prefs = await SharedPreferences.getInstance();
    return PreferenceManager._(prefs);
  }

  Future<bool> isOutageTrackingEnabled() async {
    return preferences.getBool(_kOutageTrackingEnabled) ?? false;
  }

  Future<void> setOutageTrackingEnabled(bool value) async {
    await preferences.setBool(_kOutageTrackingEnabled, value);
  }

}