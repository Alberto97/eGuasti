// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'app_map_state.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AppMapState _$AppMapStateFromJson(Map<String, dynamic> json) => AppMapState(
      (json['latitude'] as num).toDouble(),
      (json['longitude'] as num).toDouble(),
      (json['zoom'] as num).toDouble(),
    );

Map<String, dynamic> _$AppMapStateToJson(AppMapState instance) =>
    <String, dynamic>{
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'zoom': instance.zoom,
    };
