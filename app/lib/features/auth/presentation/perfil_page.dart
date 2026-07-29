import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';
import 'auth_providers.dart';

class PerfilPage extends ConsumerStatefulWidget {
  const PerfilPage({super.key});

  @override
  ConsumerState<PerfilPage> createState() => _PerfilPageState();
}

class _PerfilPageState extends ConsumerState<PerfilPage> {
  late final TextEditingController _apelido;
  bool _mostrarNomeReal = false;

  @override
  void initState() {
    super.initState();
    final usuario = ref.read(authNotifierProvider);
    _apelido = TextEditingController(text: usuario?.apelido ?? '');
    _mostrarNomeReal = usuario?.mostrarNomeReal ?? false;
  }

  Future<void> _salvar() async {
    try {
      await DioClient.instance.put('/usuarios/me', data: {
        'apelido': _apelido.text.trim(),
        'mostrarNomeReal': _mostrarNomeReal,
      });
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Perfil atualizado.')));
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
    }
  }

  @override
  Widget build(BuildContext context) {
    final usuario = ref.watch(authNotifierProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Meu perfil')),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('E-mail: ${usuario?.email ?? ''}'),
            const SizedBox(height: 16),
            TextField(controller: _apelido, decoration: const InputDecoration(labelText: 'Apelido publico')),
            SwitchListTile(
              title: const Text('Mostrar meu nome real (em vez do apelido)'),
              value: _mostrarNomeReal,
              onChanged: (v) => setState(() => _mostrarNomeReal = v),
            ),
            const SizedBox(height: 16),
            ElevatedButton(onPressed: _salvar, child: const Text('Salvar')),
          ],
        ),
      ),
    );
  }
}
