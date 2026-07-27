import 'package:flutter/material.dart';
import '../../candidatura/presentation/candidatura_list_page.dart';
import '../../mensagem/presentation/mensagem_list_page.dart';

/// Detalhe de uma mesa: abas de Candidaturas e Mensagens (RF10-RF13).
class MesaDetailPage extends StatelessWidget {
  final String mesaId;
  final String tipoJogo;
  const MesaDetailPage({super.key, required this.mesaId, required this.tipoJogo});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text(tipoJogo),
          bottom: const TabBar(tabs: [Tab(text: 'Candidaturas'), Tab(text: 'Mensagens')]),
        ),
        body: TabBarView(
          children: [
            CandidaturaListBody(mesaId: mesaId),
            MensagemListBody(mesaId: mesaId),
          ],
        ),
      ),
    );
  }
}
