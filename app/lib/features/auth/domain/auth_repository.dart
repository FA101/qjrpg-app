import 'usuario.dart';

abstract class AuthRepository {
  /// Retorna o codigo de verificacao (modo dev, sem envio real de e-mail ainda).
  Future<String> solicitarCodigo(String email);
  Future<Usuario> confirmarCodigo(String email, String codigo, {String? nome, String? celular});
}
