class Mensagem {
  final String? id;
  final String mesaId;
  final String autorId;
  final String? autorNome;
  final String? respostaDeId;
  final String conteudo;
  final String? dataHora;

  const Mensagem({
    this.id, required this.mesaId, required this.autorId, this.autorNome,
    this.respostaDeId, required this.conteudo, this.dataHora,
  });
}
