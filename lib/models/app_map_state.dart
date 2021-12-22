import 'package:json_annotation/json_annotation.dart';

part 'app_map_state.g.dart';

@JsonSerializable()
class AppMapState {
  double latitude;
  double longitude;
  double zoom;

  AppMapState(this.latitude, this.longitude, this.zoom);

  factory AppMapState.fromJson(Map<String, dynamic> value) =>
      _$AppMapStateFromJson(value);
  Map<String, dynamic> toJson() => _$AppMapStateToJson(this);
}
