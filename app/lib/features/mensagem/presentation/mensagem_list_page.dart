import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'mensagem_providers.dart';

class MensagemListBody extends ConsumerWidget {
  final String mesaId;
  const MensagemListBody({super.key, required this.mesaId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mensagensAsync = ref.watch(mensagensPorMesaProvider(mesaId));
    return mensagensAsync.when(
      data: (mensagens) {
        if (mensagens.isEmpty) return const Center(child: Text('Nenhuma mensagem ainda.'));
        return ListView(
          children: mensagens.map((m) => ListTile(title: Text(m.conteudo), subtitle: Text(m.dataHora ?? ''))).toList(),
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Erro: $e')),
    );
  }
}
