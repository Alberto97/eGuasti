import 'package:json_annotation/json_annotation.dart';

part 'response.g.dart';

@JsonSerializable()
class FeatureCollection {
  List<Feature> features;

  FeatureCollection(this.features);

  factory FeatureCollection.fromJson(Map<String, dynamic> json) =>
      _$FeatureCollectionFromJson(json);
  Map<String, dynamic> toJson() => _$FeatureCollectionToJson(this);
}

@JsonSerializable()
class Feature {
  Map<String, dynamic> attributes;

  Feature(this.attributes);

  factory Feature.fromJson(Map<String, dynamic> json) =>
      _$FeatureFromJson(json);
  Map<String, dynamic> toJson() => _$FeatureToJson(this);
}
