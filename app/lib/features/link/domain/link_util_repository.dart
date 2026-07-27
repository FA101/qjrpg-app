import 'link_util.dart';
abstract class LinkUtilRepository {
  Future<List<LinkUtil>> listarTodos();
  Future<LinkUtil> criar(LinkUtil link);
}
