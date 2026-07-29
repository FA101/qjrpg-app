import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/mesa.dart';
import 'mesa_providers.dart';
import 'mesa_detail_page.dart';

class MesaListBody extends ConsumerWidget {
  final String eventoId;
  const MesaListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mesasAsync = ref.watch(mesasPorEventoProvider(eventoId));
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: ElevatedButton.icon(
            icon: const Icon(Icons.add),
            label: const Text('Ofertar mesa'),
            onPressed: () => _abrirFormulario(context, ref),
          ),
        ),
        Expanded(
          child: mesasAsync.when(
            data: (mesas) {
              if (mesas.isEmpty) return const Center(child: Text('Nenhuma mesa ofertada ainda.'));
              return ListView(children: mesas.map((m) => _cartaoMesa(context, m)).toList());
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text('Erro: $e')),
          ),
        ),
      ],
    );
  }

  Widget _cartaoMesa(BuildContext context, Mesa m) {
    return ListTile(
      title: Text('${m.tipoJogo} - ${m.horaInicio.substring(0, 5)} as ${m.horaFim.substring(0, 5)}'),
      subtitle: Text('${m.vagas} vagas - ${m.status.name}'),
      trailing: const Icon(Icons.chevron_right),
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute(builder: (_) => MesaDetailPage(mesaId: m.id!, tipoJogo: m.tipoJogo)),
      ),
    );
  }

  void _abrirFormulario(BuildContext context, WidgetRef ref) {
    final tipoJogo = TextEditingController();
    final horaInicio = TextEditingController(text: '09:00');
    final horaFim = TextEditingController(text: '13:00');
    final vagas = TextEditingController(text: '4');

    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Ofertar mesa'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: tipoJogo, decoration: const InputDecoration(labelText: 'Tipo de jogo')),
          TextField(controller: horaInicio, decoration: const InputDecoration(labelText: 'Inicio (HH:mm)')),
          TextField(controller: horaFim, decoration: const InputDecoration(labelText: 'Fim (HH:mm)')),
          TextField(controller: vagas, decoration: const InputDecoration(labelText: 'Vagas'), keyboardType: TextInputType.number),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
          ElevatedButton(
            onPressed: () async {
              try {
                await ref.read(mesaRepositoryProvider).ofertar(Mesa(
                      eventoId: eventoId,
                      gameMasterId: AuthSession.usuarioId!,
                      tipoJogo: tipoJogo.text.trim(),
                      horaInicio: normalizarHora(horaInicio.text.trim()),
                      horaFim: normalizarHora(horaFim.text.trim()),
                      vagas: int.tryParse(vagas.text.trim()) ?? 1,
                    ));
                ref.invalidate(mesasPorEventoProvider(eventoId));
                if (dialogContext.mounted) Navigator.pop(dialogContext);
              } catch (e) {
                if (dialogContext.mounted) {
                  ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
                }
              }
            },
            child: const Text('Ofertar'),
          ),
        ],
      ),
    );
  }
}
