import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/workshop.dart';
import 'workshop_providers.dart';

class WorkshopListBody extends ConsumerWidget {
  final String eventoId;
  const WorkshopListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final workshopsAsync = ref.watch(workshopsPorEventoProvider(eventoId));
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: ElevatedButton.icon(
            icon: const Icon(Icons.add),
            label: const Text('Propor workshop'),
            onPressed: () => _abrirFormulario(context, ref),
          ),
        ),
        Expanded(
          child: workshopsAsync.when(
            data: (workshops) {
              if (workshops.isEmpty) return const Center(child: Text('Nenhum workshop proposto ainda.'));
              return ListView(
                children: workshops.map((w) => ListTile(title: Text(w.tema), subtitle: Text(w.status.name))).toList(),
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
    final tema = TextEditingController();
    final descricao = TextEditingController();
    final horario = TextEditingController();

    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Propor workshop'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: tema, decoration: const InputDecoration(labelText: 'Tema')),
          TextField(controller: descricao, decoration: const InputDecoration(labelText: 'Descricao')),
          TextField(controller: horario, decoration: const InputDecoration(labelText: 'Horario desejado (HH:mm, opcional)')),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
          ElevatedButton(
            onPressed: () async {
              try {
                await ref.read(workshopRepositoryProvider).propor(Workshop(
                      usuarioId: AuthSession.usuarioId!,
                      eventoId: eventoId,
                      tema: tema.text.trim(),
                      descricao: descricao.text.trim().isEmpty ? null : descricao.text.trim(),
                      horarioDesejado: horario.text.trim().isEmpty ? null : normalizarHora(horario.text.trim()),
                    ));
                ref.invalidate(workshopsPorEventoProvider(eventoId));
                if (dialogContext.mounted) Navigator.pop(dialogContext);
              } catch (e) {
                if (dialogContext.mounted) {
                  ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
                }
              }
            },
            child: const Text('Propor'),
          ),
        ],
      ),
    );
  }
}
