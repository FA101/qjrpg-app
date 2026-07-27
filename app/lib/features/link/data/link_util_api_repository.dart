import 'package:dio/dio.dart';
import '../domain/link_util.dart';
import '../domain/link_util_repository.dart';
import 'link_util_dto.dart';

class LinkUtilApiRepository implements LinkUtilRepository {
  final Dio _dio;
  LinkUtilApiRepository(this._dio);

  @override
  Future<List<LinkUtil>> listarTodos() async {
    final r = await _dio.get('/links-uteis');
    return (r.data as List).map((j) => LinkUtilDto.fromJson(j)).toList();
  }

  @override
  Future<LinkUtil> criar(LinkUtil link) async {
    final r = await _dio.post('/links-uteis', data: LinkUtilDto.toJson(link));
    return LinkUtilDto.fromJson(r.data);
  }
}
