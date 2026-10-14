import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/tag_api_repository.dart';
import '../domain/tag.dart';
import '../domain/tag_repository.dart';

final tagRepositoryProvider = Provider<TagRepository>((ref) => TagApiRepository(DioClient.instance));

final tagsProvider = FutureProvider.autoDispose<List<Tag>>((ref) {
  return ref.watch(tagRepositoryProvider).listarTodos();
});

/// RF21: tags acumuladas pelo usuario logado.
final minhasTagsProvider = FutureProvider.autoDispose<List<Tag>>((ref) {
  return ref.watch(tagRepositoryProvider).minhasTags();
});

final tagsDoUsuarioProvider = FutureProvider.autoDispose.family<List<Tag>, String>((ref, usuarioId) {
  return ref.watch(tagRepositoryProvider).tagsDoUsuario(usuarioId);
});
