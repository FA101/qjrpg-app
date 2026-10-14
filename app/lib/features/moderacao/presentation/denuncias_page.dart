import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/denuncia.dart';
import 'denuncia_providers.dart';

/// Fila de moderacao (RF de denuncia). So Admin/Moderador conseguem chamar
/// o endpoint - se um usuario comum chegar aqui, recebe 403 do back-end.
class DenunciasPage extends ConsumerWidget {
  const DenunciasPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final denunciasAsync = ref.watch(denunciasPendentesProvider);

    Future<void> resolver(Denuncia d, StatusDenuncia status) async {
      try {
        await DioClient.instance.patch('/denuncias/${d.id}/status', data: {'status': status.name});
        ref.invalidate(denunciasPendentesProvider);
      } catch (e) {
        if (context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
        }
      }
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Denuncias pendentes')),
      body: denunciasAsync.when(
        data: (denuncias) {
          if (denuncias.isEmpty) return const Center(child: Text('Nenhuma denuncia pendente.'));
          return ListView(
            children: denuncias.map((d) => ListTile(
                  title: Text('Denunciado: ${d.usuarioDenunciadoId}'),
                  subtitle: Text('Motivo: ${d.motivo}'),
                  trailing: Row(mainAxisSize: MainAxisSize.min, children: [
                    IconButton(
                      icon: const Icon(Icons.check_circle, color: Colors.green),
                      tooltip: 'Procedente (forca troca de apelido)',
                      onPressed: () => resolver(d, StatusDenuncia.PROCEDENTE),
                    ),
                    IconButton(
                      icon: const Icon(Icons.cancel, color: Colors.grey),
                      tooltip: 'Improcedente',
                      onPressed: () => resolver(d, StatusDenuncia.IMPROCEDENTE),
                    ),
                  ]),
                )).toList(),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: ${extrairMensagemErro(e)}')),
      ),
    );
  }
}
