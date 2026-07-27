import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/produto_api_repository.dart';
import '../domain/produto.dart';
import '../domain/produto_repository.dart';

final produtoRepositoryProvider = Provider<ProdutoRepository>((ref) => ProdutoApiRepository(DioClient.instance));

// family: cada evento tem sua propria lista de produtos
final produtosPorEventoProvider = FutureProvider.autoDispose.family<List<Produto>, String>((ref, eventoId) {
  return ref.watch(produtoRepositoryProvider).listarPorEvento(eventoId);
});
