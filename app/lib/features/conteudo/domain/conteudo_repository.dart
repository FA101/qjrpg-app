import 'conteudo_institucional.dart';
abstract class ConteudoRepository {
  Future<List<ConteudoInstitucional>> listarTodos();
  Future<ConteudoInstitucional> buscarPorSecao(String secao);
}
