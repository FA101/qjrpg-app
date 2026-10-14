import 'usuario.dart';

abstract class AuthRepository {
  /// Devolve o codigo apenas em modo desenvolvimento (API com expor-codigo=true); senao null.
  Future<String?> solicitarCodigo(String email);
  Future<LoginResultado> confirmarCodigo(String email, String codigo,
      {String? nome, String? celular, String? apelido,
      String? dataNascimento, String? sexo, String? responsavelEmail});
  Future<Usuario> validarSegundoFator(String email, String desafio2fa, String codigo);
  Future<Usuario> buscarPerfil();
}
