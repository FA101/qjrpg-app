import 'package:dio/dio.dart';

/// Extrai uma mensagem legivel de um erro, incluindo os erros de negocio
/// (RegraDeNegocioException) e validacao que o GlobalExceptionHandler do
/// back-end devolve como {"erro": "..."} ou {"campo": "mensagem"}.
String extrairMensagemErro(Object erro) {
  if (erro is DioException) {
    final dados = erro.response?.data;
    if (dados is Map && dados['erro'] != null) return dados['erro'].toString();
    if (dados is Map) return dados.values.join(', ');
    return erro.message ?? 'Erro de conexao';
  }
  return erro.toString();
}

String normalizarHora(String texto) => texto.length == 5 ? '$texto:00' : texto;
