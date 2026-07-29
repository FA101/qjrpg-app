import 'usuario.dart';

abstract class AuthRepository {
  Future<String> solicitarCodigo(String email);
  Future<Usuario> confirmarCodigo(String email, String codigo, {String? nome, String? celular, String? apelido});
}
