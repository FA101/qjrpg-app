class Usuario {
  final String id;
  final String? nome;
  final String? apelido;
  final String email;
  final String papel;
  final bool mostrarNomeReal;
  /// RF38: conta suspensa pelo proprio usuario (acesso restrito ate reativar).
  final bool suspenso;
  /// RF35/RF36b: menor sem autorizacao vigente do responsavel (acesso restrito).
  final bool aguardandoAutorizacaoResponsavel;

  const Usuario({
    required this.id, this.nome, this.apelido, required this.email,
    required this.papel, this.mostrarNomeReal = false,
    this.suspenso = false, this.aguardandoAutorizacaoResponsavel = false,
  });

  bool get acessoRestrito => suspenso || aguardandoAutorizacaoResponsavel;
  bool get ehAdmin => papel == 'ADMIN';
  bool get ehModeracao => papel == 'ADMIN' || papel == 'MODERADOR';
}

class LoginResultado {
  final Usuario? usuario;
  final bool totpPendente;
  /// Emitido pela API quando o 2FA esta ativo; deve ser reenviado com o codigo TOTP.
  final String? desafio2fa;
  const LoginResultado({this.usuario, this.totpPendente = false, this.desafio2fa});
}
