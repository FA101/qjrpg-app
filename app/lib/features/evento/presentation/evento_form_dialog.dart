import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/evento.dart';
import 'evento_providers.dart';

Future<void> abrirFormularioEvento(BuildContext context, WidgetRef ref) {
  final nome = TextEditingController();
  final local = TextEditingController();
  final linkMapa = TextEditingController();
  final horaInicio = TextEditingController(text: '09:00');
  final horaFim = TextEditingController(text: '22:00');

  return showDialog(
    context: context,
    builder: (dialogContext) => AlertDialog(
      title: const Text('Novo evento'),
      content: SingleChildScrollView(
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: nome, decoration: const InputDecoration(labelText: 'Nome')),
          TextField(controller: local, decoration: const InputDecoration(labelText: 'Local')),
          TextField(controller: linkMapa, decoration: const InputDecoration(labelText: 'Link do mapa (opcional)')),
          TextField(controller: horaInicio, decoration: const InputDecoration(labelText: 'Janela - inicio (HH:mm)')),
          TextField(controller: horaFim, decoration: const InputDecoration(labelText: 'Janela - fim (HH:mm)')),
        ]),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
        ElevatedButton(
          onPressed: () async {
            try {
              await ref.read(eventoRepositoryProvider).criar(Evento(
                    nome: nome.text.trim(),
                    local: local.text.trim(),
                    linkMapa: linkMapa.text.trim().isEmpty ? null : linkMapa.text.trim(),
                    status: StatusEvento.PLANEJADO,
                    horaInicioJanela: normalizarHora(horaInicio.text.trim()),
                    horaFimJanela: normalizarHora(horaFim.text.trim()),
                  ));
              ref.invalidate(eventosProvider);
              if (dialogContext.mounted) Navigator.pop(dialogContext);
            } catch (e) {
              if (dialogContext.mounted) {
                ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
              }
            }
          },
          child: const Text('Criar'),
        ),
      ],
    ),
  );
}
