import 'produto.dart';
abstract class ProdutoRepository {
  Future<List<Produto>> listarPorEvento(String eventoId);
  Future<Produto> criar(Produto produto);
  Future<void> excluir(String id);
}
