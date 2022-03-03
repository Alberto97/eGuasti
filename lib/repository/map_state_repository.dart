import 'dart:convert';
import 'dart:io';

import 'package:eguasti/models/app_map_state.dart';
import 'package:path_provider/path_provider.dart';

class MapStateRepository {
  Future<String> get _localPath async {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }

  Future<File> get _localFile async {
    final path = await _localPath;
    return File('$path/map_state.json');
  }

  Future<AppMapState?> readState() async {
    try {
      final file = await _localFile;

      final contents = await file.readAsString();
      final json = jsonDecode(contents);

      return AppMapState.fromJson(json);
    } catch (e) {
      return null;
    }
  }

  Future<void> writeState(AppMapState counter) async {
    final file = await _localFile;

    final json = counter.toJson();
    final value = jsonEncode(json);

    file.writeAsString(value);
  }
}
