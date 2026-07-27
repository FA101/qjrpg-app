import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../domain/mesa.dart';
import 'mesa_providers.dart';
import 'mesa_detail_page.dart';

/// Conteudo (sem Scaffold/AppBar proprios) para poder ser usado dentro de
/// uma aba (TabBarView) na tela de detalhe do Evento.
class MesaListBody extends ConsumerWidget {
  final String eventoId;
  const MesaListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mesasAsync = ref.watch(mesasPorEventoProvider(eventoId));
    return mesasAsync.when(
      data: (mesas) {
        if (mesas.isEmpty) return const Center(child: Text('Nenhuma mesa ofertada ainda.'));
        return ListView(
          children: mesas.map((m) => _cartaoMesa(context, m)).toList(),
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Erro: $e')),
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
}
