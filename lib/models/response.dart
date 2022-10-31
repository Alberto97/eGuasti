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
  Attributes attributes;

  Feature(this.attributes);

  factory Feature.fromJson(Map<String, dynamic> json) =>
      _$FeatureFromJson(json);
  Map<String, dynamic> toJson() => _$FeatureToJson(this);
}

@JsonSerializable()
class Attributes {
  @JsonKey(name: "causa_disalimentazione")
  String causaDisalimentazione;
  @JsonKey(name: "causa_interruzione")
  String causaInterruzione;
  int comune;
  @JsonKey(name: "data_interruzione")
  String dataInterruzione;
  @JsonKey(name: "data_prev_ripristino")
  String dataPrevRipristino;
  @JsonKey(name: "dataultimoaggiornamento")
  String dataUltimoAggiornamento;
  @JsonKey(name: "descrizione_territoriale")
  String descrizioneTerritoriale;
  @JsonKey(name: "id_interruzione")
  int idInterruzione;
  double latitudine;
  double longitudine;
  @JsonKey(name: "num_cli_disalim")
  int numCliDisalim;
  String provincia;
  String regione;

  Attributes(
    this.causaDisalimentazione,
    this.causaInterruzione,
    this.comune,
    this.dataInterruzione,
    this.dataPrevRipristino,
    this.dataUltimoAggiornamento,
    this.descrizioneTerritoriale,
    this.idInterruzione,
    this.latitudine,
    this.longitudine,
    this.numCliDisalim,
    this.provincia,
    this.regione,
  );

  factory Attributes.fromJson(Map<String, dynamic> json) =>
      _$AttributesFromJson(json);
  Map<String, dynamic> toJson() => _$AttributesToJson(this);
}
