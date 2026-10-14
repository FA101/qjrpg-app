import 'package:dio/dio.dart';
import '../../../core/auth/auth_session.dart';
import '../domain/auth_repository.dart';
import '../domain/usuario.dart';

class AuthApiRepository implements AuthRepository {
  final Dio _dio;
  AuthApiRepository(this._dio);

  @override
  Future<String?> solicitarCodigo(String email) async {
    final r = await _dio.post('/auth/solicitar-codigo', data: {'email': email});
    return r.data['codigoParaTeste'] as String?;
  }

  @override
  Future<LoginResultado> confirmarCodigo(String email, String codigo,
      {String? nome, String? celular, String? apelido,
      String? dataNascimento, String? sexo, String? responsavelEmail}) async {
    final r = await _dio.post('/auth/confirmar-codigo', data: {
      'email': email, 'codigo': codigo, 'nome': nome, 'celular': celular, 'apelido': apelido,
      'dataNascimento': dataNascimento, 'sexo': sexo, 'responsavelEmail': responsavelEmail,
    });
    final dados = r.data as Map<String, dynamic>;

    if (dados['totpPendente'] == true) {
      return LoginResultado(totpPendente: true, desafio2fa: dados['desafio2fa'] as String?);
    }

    AuthSession.token = dados['token'] as String;
    AuthSession.usuarioId = dados['usuarioId'] as String;
    AuthSession.papel = dados['papel'] as String;
    await AuthSession.salvar();

    return LoginResultado(usuario: Usuario(
      id: dados['usuarioId'] as String,
      nome: dados['nome'] as String?,
      apelido: dados['apelido'] as String?,
      email: dados['email'] as String,
      papel: dados['papel'] as String,
    ));
  }

  @override
  Future<Usuario> validarSegundoFator(String email, String desafio2fa, String codigo) async {
    final r = await _dio.post('/auth/2fa/validar',
        data: {'email': email, 'desafio2fa': desafio2fa, 'codigo': codigo});
    final dados = r.data as Map<String, dynamic>;

    AuthSession.token = dados['token'] as String;
    AuthSession.usuarioId = dados['usuarioId'] as String;
    AuthSession.papel = dados['papel'] as String;
    await AuthSession.salvar();

    return Usuario(
      id: dados['usuarioId'] as String,
      nome: dados['nome'] as String?,
      apelido: dados['apelido'] as String?,
      email: dados['email'] as String,
      papel: dados['papel'] as String,
    );
  }

  @override
  Future<Usuario> buscarPerfil() async {
    final r = await _dio.get('/usuarios/me');
    final dados = r.data as Map<String, dynamic>;
    AuthSession.usuarioId = dados['id'] as String;
    AuthSession.papel = dados['papel'] as String;
    return Usuario(
      id: dados['id'] as String,
      apelido: dados['apelido'] as String?,
      email: dados['email'] as String,
      papel: dados['papel'] as String,
      mostrarNomeReal: dados['mostrarNomeReal'] as bool? ?? false,
      suspenso: dados['suspenso'] as bool? ?? false,
      aguardandoAutorizacaoResponsavel: dados['aguardandoAutorizacaoResponsavel'] as bool? ?? false,
    );
  }
}
