import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/mensagem.dart';
import 'mensagem_providers.dart';

class MensagemListBody extends ConsumerStatefulWidget {
  final String mesaId;
  const MensagemListBody({super.key, required this.mesaId});

  @override
  ConsumerState<MensagemListBody> createState() => _MensagemListBodyState();
}

class _MensagemListBodyState extends ConsumerState<MensagemListBody> {
  final _controller = TextEditingController();

  Future<void> _enviar() async {
    if (_controller.text.trim().isEmpty) return;
    try {
      await ref.read(mensagemRepositoryProvider).enviar(Mensagem(
            mesaId: widget.mesaId,
            autorId: AuthSession.usuarioId!,
            conteudo: _controller.text.trim(),
          ));
      _controller.clear();
      ref.invalidate(mensagensPorMesaProvider(widget.mesaId));
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
    }
  }

  @override
  Widget build(BuildContext context) {
    final mensagensAsync = ref.watch(mensagensPorMesaProvider(widget.mesaId));
    return Column(
      children: [
        Expanded(
          child: mensagensAsync.when(
            data: (mensagens) {
              if (mensagens.isEmpty) return const Center(child: Text('Nenhuma mensagem ainda.'));
              return ListView(
                children: mensagens.map((m) => ListTile(title: Text(m.conteudo), subtitle: Text(m.dataHora ?? ''))).toList(),
              );
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text('Erro: $e')),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(8),
          child: Row(children: [
            Expanded(
              child: TextField(
                controller: _controller,
                decoration: const InputDecoration(hintText: 'Escreva uma mensagem publica...'),
              ),
            ),
            IconButton(icon: const Icon(Icons.send), onPressed: _enviar),
          ]),
        ),
      ],
    );
  }
}
