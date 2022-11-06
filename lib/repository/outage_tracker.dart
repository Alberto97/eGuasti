import 'package:hive_flutter/hive_flutter.dart';

class OutageTracker {

  late Box<dynamic> box;

  OutageTracker() {
    _init();
  }

  void _init() async {
    box = await Hive.openBox('trackedOutages');
  }

  void track(int id) {
    box.put(id, {});
  }

  void untrack(int id) {
    box.delete(id);
  }

  bool isTracking(int id) {
    return box.get(id) != null;
  }

  List<int> getTracked() {
    return box.keys.toList().cast<int>();
  }

}