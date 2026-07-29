import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/produto.dart';
import 'produto_providers.dart';

class ProdutoListBody extends ConsumerWidget {
  final String eventoId;
  const ProdutoListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final produtosAsync = ref.watch(produtosPorEventoProvider(eventoId));
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: ElevatedButton.icon(
            icon: const Icon(Icons.add),
            label: const Text('Divulgar produto'),
            onPressed: () => _abrirFormulario(context, ref),
          ),
        ),
        Expanded(
          child: produtosAsync.when(
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
          ),
        ),
      ],
    );
  }

  void _abrirFormulario(BuildContext context, WidgetRef ref) {
    final titulo = TextEditingController();
    final descricao = TextEditingController();
    final linkExterno = TextEditingController();
    TipoProduto tipo = TipoProduto.DIGITAL;

    showDialog(
      context: context,
      builder: (dialogContext) => StatefulBuilder(
        builder: (dialogContext, setState) => AlertDialog(
          title: const Text('Divulgar produto'),
          content: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(controller: titulo, decoration: const InputDecoration(labelText: 'Titulo')),
            TextField(controller: descricao, decoration: const InputDecoration(labelText: 'Descricao')),
            TextField(controller: linkExterno, decoration: const InputDecoration(labelText: 'Link externo (opcional)')),
            DropdownButton<TipoProduto>(
              value: tipo,
              items: TipoProduto.values.map((t) => DropdownMenuItem(value: t, child: Text(t.name))).toList(),
              onChanged: (v) => setState(() => tipo = v!),
            ),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
            ElevatedButton(
              onPressed: () async {
                try {
                  await ref.read(produtoRepositoryProvider).criar(Produto(
                        usuarioId: AuthSession.usuarioId!,
                        eventoId: eventoId,
                        tipo: tipo,
                        titulo: titulo.text.trim(),
                        descricao: descricao.text.trim().isEmpty ? null : descricao.text.trim(),
                        linkExterno: linkExterno.text.trim().isEmpty ? null : linkExterno.text.trim(),
                      ));
                  ref.invalidate(produtosPorEventoProvider(eventoId));
                  if (dialogContext.mounted) Navigator.pop(dialogContext);
                } catch (e) {
                  if (dialogContext.mounted) {
                    ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
                  }
                }
              },
              child: const Text('Divulgar'),
            ),
          ],
        ),
      ),
    );
  }
}
