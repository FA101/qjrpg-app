import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import 'auth_providers.dart';

class LoginPage extends ConsumerStatefulWidget {
  const LoginPage({super.key});

  @override
  ConsumerState<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends ConsumerState<LoginPage> {
  final _emailController = TextEditingController();
  final _codigoController = TextEditingController();
  final _nomeController = TextEditingController();
  final _celularController = TextEditingController();
  final _apelidoController = TextEditingController();
  final _focusCodigo = FocusNode();

  bool _codigoEnviado = false;
  bool _carregando = false;
  String? _erro;
  String? _codigoDeTeste;

  Future<void> _solicitarCodigo() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      final codigo = await ref.read(authNotifierProvider.notifier).solicitarCodigo(_emailController.text.trim());
      setState(() { _codigoEnviado = true; _codigoDeTeste = codigo; });
      Future.delayed(const Duration(milliseconds: 100), () => _focusCodigo.requestFocus());
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  Future<void> _confirmarCodigo() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      await ref.read(authNotifierProvider.notifier).confirmarCodigo(
            _emailController.text.trim(),
            _codigoController.text.trim(),
            nome: _nomeController.text.trim().isEmpty ? null : _nomeController.text.trim(),
            celular: _celularController.text.trim().isEmpty ? null : _celularController.text.trim(),
            apelido: _apelidoController.text.trim().isEmpty ? null : _apelidoController.text.trim(),
          );
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Entrar - Quero Jogar RPG')),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _emailController,
              enabled: !_codigoEnviado,
              decoration: const InputDecoration(labelText: 'E-mail'),
              keyboardType: TextInputType.emailAddress,
              onSubmitted: (_) { if (!_codigoEnviado) _solicitarCodigo(); },
            ),
            const SizedBox(height: 16),
            if (!_codigoEnviado)
              ElevatedButton(
                onPressed: _carregando ? null : _solicitarCodigo,
                child: Text(_carregando ? 'Enviando...' : 'Enviar codigo'),
              ),
            if (_codigoEnviado) ...[
              if (_codigoDeTeste != null)
                Text('Modo desenvolvimento - codigo: $_codigoDeTeste',
                    style: const TextStyle(color: Colors.orange)),
              const SizedBox(height: 8),
              TextField(
                controller: _codigoController,
                focusNode: _focusCodigo,
                decoration: const InputDecoration(labelText: 'Codigo recebido'),
                keyboardType: TextInputType.number,
                onSubmitted: (_) => _confirmarCodigo(),
              ),
              const SizedBox(height: 8),
              const Text('Preencha abaixo so no seu primeiro acesso:', style: TextStyle(fontSize: 12)),
              TextField(
                controller: _apelidoController,
                decoration: const InputDecoration(labelText: 'Apelido publico (unico, ex: estilo Discord)'),
                onSubmitted: (_) => _confirmarCodigo(),
              ),
              TextField(
                controller: _nomeController,
                decoration: const InputDecoration(labelText: 'Nome real (privado por padrao)'),
                onSubmitted: (_) => _confirmarCodigo(),
              ),
              TextField(
                controller: _celularController,
                decoration: const InputDecoration(labelText: 'Celular'),
                onSubmitted: (_) => _confirmarCodigo(),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _carregando ? null : _confirmarCodigo,
                child: Text(_carregando ? 'Confirmando...' : 'Confirmar e entrar'),
              ),
            ],
            if (_erro != null) ...[
              const SizedBox(height: 16),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(child: SelectableText(_erro!, style: const TextStyle(color: Colors.red))),
                  IconButton(
                    icon: const Icon(Icons.copy, size: 18),
                    tooltip: 'Copiar mensagem de erro',
                    onPressed: () {
                      Clipboard.setData(ClipboardData(text: _erro!));
                      ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Erro copiado para a area de transferencia.')));
                    },
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}