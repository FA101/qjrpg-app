import 'mensagem.dart';
abstract class MensagemRepository {
  Future<List<Mensagem>> listarPorMesa(String mesaId);
  Future<Mensagem> enviar(Mensagem mensagem);
  Future<void> excluir(String id);
}
