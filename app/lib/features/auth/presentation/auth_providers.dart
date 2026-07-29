import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/network/dio_client.dart';
import '../data/auth_api_repository.dart';
import '../domain/auth_repository.dart';
import '../domain/usuario.dart';

final authRepositoryProvider = Provider<AuthRepository>((ref) => AuthApiRepository(DioClient.instance));

class AuthNotifier extends StateNotifier<Usuario?> {
  final AuthRepository _repository;
  AuthNotifier(this._repository) : super(null);

  Future<String> solicitarCodigo(String email) => _repository.solicitarCodigo(email);

  Future<void> confirmarCodigo(String email, String codigo, {String? nome, String? celular, String? apelido}) async {
    state = await _repository.confirmarCodigo(email, codigo, nome: nome, celular: celular, apelido: apelido);
  }

  void sair() {
    AuthSession.limpar();
    state = null;
  }
}

final authNotifierProvider = StateNotifierProvider<AuthNotifier, Usuario?>((ref) {
  return AuthNotifier(ref.watch(authRepositoryProvider));
});
