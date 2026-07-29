/// Guarda o token JWT em memoria (sessao perdida ao recarregar a pagina -
/// simplificacao proposital desta rodada; persistir com seguranca fica para
/// depois, com flutter_secure_storage). O DioClient le AuthSession.token
/// a cada requisicao para montar o header Authorization.
class AuthSession {
  static String? token;
  static String? usuarioId;
  static String? papel;

  static bool get autenticado => token != null;

  static void limpar() {
    token = null;
    usuarioId = null;
    papel = null;
  }
}
