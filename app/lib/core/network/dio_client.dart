import 'package:dio/dio.dart';
import '../auth/auth_session.dart';

class DioClient {
  DioClient._();

  static const String baseUrl = 'http://localhost:8080/api';

  static final Dio instance = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {'Content-Type': 'application/json'},
    ),
  )..interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        final token = AuthSession.token;
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
    ));
}
