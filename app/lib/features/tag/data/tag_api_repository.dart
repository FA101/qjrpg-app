import 'package:dio/dio.dart';
import '../domain/tag.dart';
import '../domain/tag_repository.dart';
import 'tag_dto.dart';

class TagApiRepository implements TagRepository {
  final Dio _dio;
  TagApiRepository(this._dio);

  @override
  Future<List<Tag>> listarTodos() async {
    final r = await _dio.get('/tags');
    return (r.data as List).map((j) => TagDto.fromJson(j)).toList();
  }

  @override
  Future<Tag> criar(Tag tag) async {
    final r = await _dio.post('/tags', data: TagDto.toJson(tag));
    return TagDto.fromJson(r.data);
  }

  @override
  Future<void> excluir(String id) async => _dio.delete('/tags/$id');

  @override
  Future<List<Tag>> tagsDoUsuario(String usuarioId) async {
    final r = await _dio.get('/usuarios/$usuarioId/tags');
    return (r.data as List).map((j) => TagDto.fromJson(j)).toList();
  }

  @override
  Future<List<Tag>> minhasTags() async {
    final r = await _dio.get('/usuarios/me/tags');
    return (r.data as List).map((j) => TagDto.fromJson(j)).toList();
  }

  @override
  Future<void> atribuirAoUsuario(String usuarioId, String tagId) async =>
      _dio.post('/usuarios/$usuarioId/tags/$tagId');

  @override
  Future<void> removerDoUsuario(String usuarioId, String tagId) async =>
      _dio.delete('/usuarios/$usuarioId/tags/$tagId');
}
