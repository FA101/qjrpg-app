import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/link_util_api_repository.dart';
import '../domain/link_util.dart';
import '../domain/link_util_repository.dart';

final linkUtilRepositoryProvider = Provider<LinkUtilRepository>((ref) => LinkUtilApiRepository(DioClient.instance));

final linksUteisProvider = FutureProvider.autoDispose<List<LinkUtil>>((ref) {
  return ref.watch(linkUtilRepositoryProvider).listarTodos();
});
