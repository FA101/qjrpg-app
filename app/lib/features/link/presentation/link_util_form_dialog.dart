import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/link_util.dart';
import 'link_util_providers.dart';

Future<void> abrirFormularioLink(BuildContext context, WidgetRef ref) {
  final titulo = TextEditingController();
  final url = TextEditingController();
  final categoria = TextEditingController();

  return showDialog(
    context: context,
    builder: (dialogContext) {
      Future<void> criar() async {
        try {
          await ref.read(linkUtilRepositoryProvider).criar(
              LinkUtil(titulo: titulo.text.trim(), url: url.text.trim(), categoria: categoria.text.trim()));
          ref.invalidate(linksUteisProvider);
          if (dialogContext.mounted) Navigator.pop(dialogContext);
        } catch (e) {
          if (dialogContext.mounted) {
            ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
          }
        }
      }

      return AlertDialog(
        title: const Text('Novo link'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: titulo, decoration: const InputDecoration(labelText: 'Titulo')),
          TextField(controller: url, decoration: const InputDecoration(labelText: 'URL')),
          TextField(
            controller: categoria,
            decoration: const InputDecoration(labelText: 'Categoria'),
            onSubmitted: (_) => criar(),
          ),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
          ElevatedButton(onPressed: criar, child: const Text('Criar')),
        ],
      );
    },
  );
}
