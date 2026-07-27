import '../domain/mensagem.dart';

class MensagemDto {
  static Mensagem fromJson(Map<String, dynamic> json) => Mensagem(
        id: json['id'] as String?,
        mesaId: json['mesaId'] as String,
        autorId: json['autorId'] as String,
        respostaDeId: json['respostaDeId'] as String?,
        conteudo: json['conteudo'] as String,
        dataHora: json['dataHora'] as String?,
      );

  static Map<String, dynamic> toJson(Mensagem m) => {
        'mesaId': m.mesaId, 'autorId': m.autorId, 'respostaDeId': m.respostaDeId, 'conteudo': m.conteudo,
      };
}
