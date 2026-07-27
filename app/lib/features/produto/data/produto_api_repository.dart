import 'package:dio/dio.dart';
import '../domain/produto.dart';
import '../domain/produto_repository.dart';
import 'produto_dto.dart';

class ProdutoApiRepository implements ProdutoRepository {
  final Dio _dio;
  ProdutoApiRepository(this._dio);

  @override
  Future<List<Produto>> listarPorEvento(String eventoId) async {
    final r = await _dio.get('/produtos', queryParameters: {'eventoId': eventoId});
    return (r.data as List).map((j) => ProdutoDto.fromJson(j)).toList();
  }

  @override
  Future<Produto> criar(Produto produto) async {
    final r = await _dio.post('/produtos', data: ProdutoDto.toJson(produto));
    return ProdutoDto.fromJson(r.data);
  }

  @override
  Future<void> excluir(String id) async => _dio.delete('/produtos/$id');
}
