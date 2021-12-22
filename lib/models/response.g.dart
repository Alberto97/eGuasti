// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AppResponse _$AppResponseFromJson(Map<String, dynamic> json) => AppResponse(
      json['exceededTransferLimit'] as bool,
      (json['features'] as List<dynamic>)
          .map((e) => Feature.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$AppResponseToJson(AppResponse instance) =>
    <String, dynamic>{
      'exceededTransferLimit': instance.exceededTransferLimit,
      'features': instance.features,
    };

Feature _$FeatureFromJson(Map<String, dynamic> json) => Feature(
      Attributes.fromJson(json['attributes'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$FeatureToJson(Feature instance) => <String, dynamic>{
      'attributes': instance.attributes,
    };

Attributes _$AttributesFromJson(Map<String, dynamic> json) => Attributes(
      json['causa_disalimentazione'] as String,
      json['causa_interruzione'] as String,
      json['comune'] as int,
      json['data_interruzione'] as String,
      json['data_prev_ripristino'] as String,
      json['dataultimoaggiornamento'] as String,
      json['descrizione_territoriale'] as String,
      json['id_interruzione'] as int,
      (json['latitudine'] as num).toDouble(),
      (json['longitudine'] as num).toDouble(),
      json['num_cli_disalim'] as int,
      json['provincia'] as String,
      json['regione'] as String,
    );

Map<String, dynamic> _$AttributesToJson(Attributes instance) =>
    <String, dynamic>{
      'causa_disalimentazione': instance.causaDisalimentazione,
      'causa_interruzione': instance.causaInterruzione,
      'comune': instance.comune,
      'data_interruzione': instance.dataInterruzione,
      'data_prev_ripristino': instance.dataPrevRipristino,
      'dataultimoaggiornamento': instance.dataUltimoAggiornamento,
      'descrizione_territoriale': instance.descrizioneTerritoriale,
      'id_interruzione': instance.idInterruzione,
      'latitudine': instance.latitudine,
      'longitudine': instance.longitudine,
      'num_cli_disalim': instance.numCliDisalim,
      'provincia': instance.provincia,
      'regione': instance.regione,
    };
