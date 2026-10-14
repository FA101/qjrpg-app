import 'tag.dart';

abstract class TagRepository {
  Future<List<Tag>> listarTodos();
  Future<Tag> criar(Tag tag);
  Future<void> excluir(String id);
  Future<List<Tag>> tagsDoUsuario(String usuarioId);
  Future<List<Tag>> minhasTags();
  Future<void> atribuirAoUsuario(String usuarioId, String tagId);
  Future<void> removerDoUsuario(String usuarioId, String tagId);
}
