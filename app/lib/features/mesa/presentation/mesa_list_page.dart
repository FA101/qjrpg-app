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
            onPressed: () => _abrirFormulario(context, ref, mesasAsync.value?.length ?? 0),
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
    final numero = m.numero != null ? '#${m.numero} - ' : '';
    final sistema = m.sistemaJogo != null ? ' (${m.sistemaJogo})' : '';
    final vagas = m.vagasDisponiveis != null ? '${m.vagasDisponiveis}/${m.vagasTotais} vagas' : '${m.vagasTotais} vagas';
    return ListTile(
      title: Text('$numero${m.tituloAventura ?? m.tipoJogo}$sistema'),
      subtitle: Text(
          '${m.gameMasterNome ?? "Mestre"} - ${m.horaInicio.substring(0, 5)} as ${m.horaFim.substring(0, 5)} - $vagas - ${m.status.name}'),
      trailing: const Icon(Icons.chevron_right),
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute(builder: (_) => MesaDetailPage(mesaId: m.id!, tipoJogo: m.tituloAventura ?? m.tipoJogo)),
      ),
    );
  }

  void _abrirFormulario(BuildContext context, WidgetRef ref, int quantidadeAtual) {
    final numero = TextEditingController(text: '${quantidadeAtual + 1}');
    final tipoJogo = TextEditingController(text: 'RPG');
    final sistemaJogo = TextEditingController();
    final tituloAventura = TextEditingController();
    final sinopse = TextEditingController();
    final palavrasChave = TextEditingController();
    final observacoes = TextEditingController();
    final faixaEtaria = TextEditingController(text: 'Livre');
    final horaInicio = TextEditingController(text: '09:00');
    final horaFim = TextEditingController(text: '13:00');
    final vagasTotais = TextEditingController(text: '4');
    final vagasReservadas = TextEditingController(text: '0');

    Future<void> ofertar(BuildContext dialogContext) async {
      try {
        await ref.read(mesaRepositoryProvider).ofertar(Mesa(
              eventoId: eventoId,
              gameMasterId: AuthSession.usuarioId!,
              numero: int.tryParse(numero.text.trim()),
              tipoJogo: tipoJogo.text.trim(),
              sistemaJogo: sistemaJogo.text.trim().isEmpty ? null : sistemaJogo.text.trim(),
              tituloAventura: tituloAventura.text.trim().isEmpty ? null : tituloAventura.text.trim(),
              sinopse: sinopse.text.trim().isEmpty ? null : sinopse.text.trim(),
              palavrasChave: palavrasChave.text.trim().isEmpty ? null : palavrasChave.text.trim(),
              observacoes: observacoes.text.trim().isEmpty ? null : observacoes.text.trim(),
              faixaEtaria: faixaEtaria.text.trim().isEmpty ? null : faixaEtaria.text.trim(),
              horaInicio: normalizarHora(horaInicio.text.trim()),
              horaFim: normalizarHora(horaFim.text.trim()),
              vagasTotais: int.tryParse(vagasTotais.text.trim()) ?? 1,
              vagasReservadas: int.tryParse(vagasReservadas.text.trim()) ?? 0,
            ));
        ref.invalidate(mesasPorEventoProvider(eventoId));
        if (dialogContext.mounted) Navigator.pop(dialogContext);
      } catch (e) {
        if (dialogContext.mounted) {
          ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
        }
      }
    }

    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Ofertar mesa'),
        content: SingleChildScrollView(
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(controller: numero, decoration: const InputDecoration(labelText: 'Numero da mesa'), keyboardType: TextInputType.number),
            TextField(controller: tipoJogo, decoration: const InputDecoration(labelText: 'Tipo (RPG/BG/CG/WG)')),
            TextField(controller: sistemaJogo, decoration: const InputDecoration(labelText: 'Sistema (ex: D&D 2024)')),
            TextField(controller: tituloAventura, decoration: const InputDecoration(labelText: 'Titulo da aventura')),
            TextField(controller: sinopse, decoration: const InputDecoration(labelText: 'Sinopse/contexto'), maxLines: 3),
            TextField(controller: palavrasChave, decoration: const InputDecoration(labelText: 'Palavras-chave (separadas por virgula)')),
            TextField(controller: observacoes, decoration: const InputDecoration(labelText: 'Observacoes/extras'), maxLines: 2),
            TextField(controller: faixaEtaria, decoration: const InputDecoration(labelText: 'Classificacao etaria')),
            TextField(controller: horaInicio, decoration: const InputDecoration(labelText: 'Inicio (HH:mm)')),
            TextField(controller: horaFim, decoration: const InputDecoration(labelText: 'Fim (HH:mm)')),
            TextField(controller: vagasTotais, decoration: const InputDecoration(labelText: 'Vagas totais'), keyboardType: TextInputType.number),
            TextField(
              controller: vagasReservadas,
              decoration: const InputDecoration(labelText: 'Vagas reservadas (convidados)'),
              keyboardType: TextInputType.number,
              onSubmitted: (_) => ofertar(dialogContext),
            ),
          ]),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
          ElevatedButton(onPressed: () => ofertar(dialogContext), child: const Text('Ofertar')),
        ],
      ),
    );
  }
}
