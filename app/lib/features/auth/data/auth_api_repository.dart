import 'package:dio/dio.dart';
import '../../../core/auth/auth_session.dart';
import '../domain/auth_repository.dart';
import '../domain/usuario.dart';

class AuthApiRepository implements AuthRepository {
  final Dio _dio;
  AuthApiRepository(this._dio);

  @override
  Future<String> solicitarCodigo(String email) async {
    final r = await _dio.post('/auth/solicitar-codigo', data: {'email': email});
    return r.data['codigoParaTeste'] as String;
  }

  @override
  Future<Usuario> confirmarCodigo(String email, String codigo, {String? nome, String? celular}) async {
    final r = await _dio.post('/auth/confirmar-codigo', data: {
      'email': email, 'codigo': codigo, 'nome': nome, 'celular': celular,
    });
    final dados = r.data as Map<String, dynamic>;

    AuthSession.token = dados['token'] as String;
    AuthSession.usuarioId = dados['usuarioId'] as String;
    AuthSession.papel = dados['papel'] as String;

    return Usuario(
      id: dados['usuarioId'] as String,
      nome: dados['nome'] as String?,
      email: dados['email'] as String,
      papel: dados['papel'] as String,
    );
  }
}
