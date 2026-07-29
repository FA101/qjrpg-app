class Usuario {
  final String id;
  final String? nome;
  final String? apelido;
  final String email;
  final String papel;
  final bool mostrarNomeReal;

  const Usuario({
    required this.id, this.nome, this.apelido, required this.email,
    required this.papel, this.mostrarNomeReal = false,
  });
}
