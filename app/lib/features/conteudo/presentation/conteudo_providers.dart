import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/conteudo_api_repository.dart';
import '../domain/conteudo_institucional.dart';
import '../domain/conteudo_repository.dart';

final conteudoRepositoryProvider = Provider<ConteudoRepository>((ref) => ConteudoApiRepository(DioClient.instance));

final conteudoPorSecaoProvider = FutureProvider.autoDispose.family<ConteudoInstitucional, String>((ref, secao) {
  return ref.watch(conteudoRepositoryProvider).buscarPorSecao(secao);
});
