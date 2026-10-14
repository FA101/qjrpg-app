import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/network/dio_client.dart';
import '../data/auth_api_repository.dart';
import '../domain/auth_repository.dart';
import '../domain/usuario.dart';

final authRepositoryProvider = Provider<AuthRepository>((ref) => AuthApiRepository(DioClient.instance));

class AuthNotifier extends StateNotifier<Usuario?> {
  final AuthRepository _repository;
  String? _desafio2fa;
  AuthNotifier(this._repository) : super(null);

  Future<String?> solicitarCodigo(String email) => _repository.solicitarCodigo(email);

  /// Retorna true se o 2FA estiver pendente (login ainda nao concluido).
  Future<bool> confirmarCodigo(String email, String codigo,
      {String? nome, String? celular, String? apelido,
      String? dataNascimento, String? sexo, String? responsavelEmail}) async {
    final resultado = await _repository.confirmarCodigo(email, codigo,
        nome: nome, celular: celular, apelido: apelido,
        dataNascimento: dataNascimento, sexo: sexo, responsavelEmail: responsavelEmail);
    if (resultado.totpPendente) {
      _desafio2fa = resultado.desafio2fa;
      return true;
    }
    state = await _perfilCompleto(resultado.usuario!);
    return false;
  }

  Future<void> validarSegundoFator(String email, String codigo) async {
    final desafio = _desafio2fa;
    if (desafio == null) {
      throw StateError('Sessao de login expirada. Solicite um novo codigo por e-mail.');
    }
    final usuario = await _repository.validarSegundoFator(email, desafio, codigo);
    _desafio2fa = null;
    state = await _perfilCompleto(usuario);
  }

  /// Recarrega o perfil apos mudancas de estado (ex.: reativar conta).
  Future<void> recarregarPerfil() async {
    state = await _repository.buscarPerfil();
  }

  /// O login devolve so os dados basicos; o perfil traz os estados de acesso (suspenso, pendente).
  Future<Usuario> _perfilCompleto(Usuario basico) async {
    try {
      return await _repository.buscarPerfil();
    } catch (_) {
      return basico;
    }
  }

  void definirUsuario(Usuario usuario) { state = usuario; }

  Future<void> sair() async {
    await AuthSession.limpar();
    state = null;
  }
}

final authNotifierProvider = StateNotifierProvider<AuthNotifier, Usuario?>((ref) {
  return AuthNotifier(ref.watch(authRepositoryProvider));
});

final sessaoInicialProvider = FutureProvider<void>((ref) async {
  await AuthSession.carregar();
  if (AuthSession.token == null) return;
  try {
    final usuario = await ref.read(authRepositoryProvider).buscarPerfil();
    ref.read(authNotifierProvider.notifier).definirUsuario(usuario);
  } catch (_) {
    await AuthSession.limpar();
  }
});
