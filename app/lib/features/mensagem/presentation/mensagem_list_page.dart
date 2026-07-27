import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'mensagem_providers.dart';

class MensagemListPage extends ConsumerWidget {
  final String mesaId;
  const MensagemListPage({super.key, required this.mesaId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mensagensAsync = ref.watch(mensagensPorMesaProvider(mesaId));
    return Scaffold(
      appBar: AppBar(title: const Text('Mensagens da mesa')),
      body: mensagensAsync.when(
        data: (mensagens) => ListView(
          children: mensagens.map((m) => ListTile(title: Text(m.conteudo), subtitle: Text(m.dataHora ?? ''))).toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
