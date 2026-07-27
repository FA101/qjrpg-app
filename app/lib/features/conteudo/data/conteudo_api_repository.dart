import 'package:dio/dio.dart';
import '../domain/conteudo_institucional.dart';
import '../domain/conteudo_repository.dart';
import 'conteudo_dto.dart';

class ConteudoApiRepository implements ConteudoRepository {
  final Dio _dio;
  ConteudoApiRepository(this._dio);

  @override
  Future<List<ConteudoInstitucional>> listarTodos() async {
    final r = await _dio.get('/conteudos');
    return (r.data as List).map((j) => ConteudoDto.fromJson(j)).toList();
  }

  @override
  Future<ConteudoInstitucional> buscarPorSecao(String secao) async {
    final r = await _dio.get('/conteudos/$secao');
    return ConteudoDto.fromJson(r.data);
  }
}
