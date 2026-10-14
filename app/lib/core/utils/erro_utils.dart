import 'package:dio/dio.dart';

/// Extrai uma mensagem legivel de um erro, incluindo os erros de negocio
/// (RegraDeNegocioException) e validacao que o GlobalExceptionHandler do
/// back-end devolve como {"erro": "..."} ou {"campo": "mensagem"}.
String extrairMensagemErro(Object erro) {
  if (erro is DioException) {
    final status = erro.response?.statusCode;
    final dados = erro.response?.data;
    if (status == 401) return 'Sessao expirada. Entre novamente.';
    if (status == 403 && !(dados is Map && dados['erro'] != null)) {
      return 'Voce nao tem permissao para esta acao.';
    }
    if (erro.response == null) return 'Sem conexao com o servidor. Verifique sua internet.';
    if (dados is Map && dados['erro'] != null) return dados['erro'].toString();
    if (dados is Map) return dados.values.join(', ');
    return erro.message ?? 'Erro de conexao';
  }
  return erro.toString();
}

String normalizarHora(String texto) => texto.length == 5 ? '$texto:00' : texto;
