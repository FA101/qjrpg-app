import 'tag.dart';

abstract class TagRepository {
  Future<List<Tag>> listarTodos();
  Future<Tag> criar(Tag tag);
  Future<void> excluir(String id);
}
