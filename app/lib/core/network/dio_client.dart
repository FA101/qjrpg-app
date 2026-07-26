import 'package:dio/dio.dart';

/// Cliente HTTP central. Um unico ponto de configuracao (base URL,
/// timeouts, interceptors futuros de autenticacao) - DRY: nenhuma
/// feature monta seu proprio Dio do zero.
class DioClient {
  DioClient._();

  // Emulador Android usa 10.0.2.2 para chegar no localhost da maquina host.
  // Rodando via Chrome (flutter run -d chrome), use localhost mesmo.
  //static const String baseUrl = 'http://10.0.2.2:8080/api';
  static const String baseUrl = 'http://localhost:8080/api';

  static final Dio instance = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {'Content-Type': 'application/json'},
    ),
  );
}
