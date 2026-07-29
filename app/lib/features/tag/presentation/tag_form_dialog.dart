import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/tag.dart';
import 'tag_providers.dart';

Future<void> abrirFormularioTag(BuildContext context, WidgetRef ref) {
  final nome = TextEditingController();
  final corHex = TextEditingController(text: '#7C3AED');
  TipoTag tipo = TipoTag.CUSTOMIZADA;

  return showDialog(
    context: context,
    builder: (dialogContext) => StatefulBuilder(
      builder: (dialogContext, setState) {
        Future<void> criar() async {
          try {
            await ref.read(tagRepositoryProvider).criar(Tag(nome: nome.text.trim(), corHex: corHex.text.trim(), tipo: tipo));
            ref.invalidate(tagsProvider);
            if (dialogContext.mounted) Navigator.pop(dialogContext);
          } catch (e) {
            if (dialogContext.mounted) {
              ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
            }
          }
        }

        return AlertDialog(
          title: const Text('Nova tag'),
          content: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(controller: nome, decoration: const InputDecoration(labelText: 'Nome')),
            TextField(
              controller: corHex,
              decoration: const InputDecoration(labelText: 'Cor (#RRGGBB)'),
              onSubmitted: (_) => criar(),
            ),
            DropdownButton<TipoTag>(
              value: tipo,
              items: TipoTag.values.map((t) => DropdownMenuItem(value: t, child: Text(t.name))).toList(),
              onChanged: (v) => setState(() => tipo = v!),
            ),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
            ElevatedButton(onPressed: criar, child: const Text('Criar')),
          ],
        );
      },
    ),
  );
}
