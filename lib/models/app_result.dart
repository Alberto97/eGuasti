class AppResult<T> {
  int? code;
  T? data;
  String? message;

  AppResult({this.code, this.data, this.message});

  bool get isSuccessful => message == null || message!.isEmpty;
}
