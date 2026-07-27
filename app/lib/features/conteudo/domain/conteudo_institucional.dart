class ConteudoInstitucional {
  final String? id;
  final String secao;
  final String titulo;
  final String corpo;
  final String autorId;
  final String? dataAtualizacao;

  const ConteudoInstitucional({this.id, required this.secao, required this.titulo, required this.corpo, required this.autorId, this.dataAtualizacao});
}
