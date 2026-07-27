import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'produto_providers.dart';

class ProdutoListBody extends ConsumerWidget {
  final String eventoId;
  const ProdutoListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final produtosAsync = ref.watch(produtosPorEventoProvider(eventoId));
    return produtosAsync.when(
      data: (produtos) {
        if (produtos.isEmpty) return const Center(child: Text('Nenhum produto divulgado ainda.'));
        return ListView(
          children: produtos
              .map((p) => ListTile(title: Text(p.titulo), subtitle: Text('${p.tipo.name} - ${p.descricao ?? ''}')))
              .toList(),
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Erro: $e')),
    );
  }
}
