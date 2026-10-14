class Usuario {
  final String id;
  final String? nome;
  final String? apelido;
  final String email;
  final String papel;
  final bool mostrarNomeReal;

  const Usuario({
    required this.id, this.nome, this.apelido, required this.email,
    required this.papel, this.mostrarNomeReal = false,
  });
}

class LoginResultado {
  final Usuario? usuario;
  final bool totpPendente;
  /// Emitido pela API quando o 2FA esta ativo; deve ser reenviado com o codigo TOTP.
  final String? desafio2fa;
  const LoginResultado({this.usuario, this.totpPendente = false, this.desafio2fa});
}
