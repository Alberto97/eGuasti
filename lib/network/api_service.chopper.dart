// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'api_service.dart';

// **************************************************************************
// ChopperGenerator
// **************************************************************************

// coverage:ignore-file
// ignore_for_file: type=lint
final class _$ApiService extends ApiService {
  _$ApiService([ChopperClient? client]) {
    if (client == null) return;
    this.client = client;
  }

  @override
  final Type definitionType = ApiService;

  @override
  Future<Response<FeatureCollection>> query(
    String where, {
    String format = "json",
    bool returnGeometry = false,
    String outFields = "*",
  }) {
    final Uri $url = Uri.parse('query');
    final Map<String, dynamic> $params = <String, dynamic>{
      'where': where,
      'f': format,
      'returnGeometry': returnGeometry,
      'outFields': outFields,
    };
    final Request $request = Request(
      'GET',
      $url,
      client.baseUrl,
      parameters: $params,
    );
    return client.send<FeatureCollection, FeatureCollection>($request);
  }
}
