import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'auth_providers.dart';

/// Login em 2 passos, sem senha: informar e-mail -> receber codigo -> confirmar.
/// Os campos de nome/celular so importam no primeiro acesso (cadastro).
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

  bool _codigoEnviado = false;
  bool _carregando = false;
  String? _erro;
  String? _codigoDeTeste; // so aparece porque ainda nao ha envio real de e-mail

  Future<void> _solicitarCodigo() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      final codigo = await ref.read(authNotifierProvider.notifier).solicitarCodigo(_emailController.text.trim());
      setState(() { _codigoEnviado = true; _codigoDeTeste = codigo; });
    } catch (e) {
      setState(() => _erro = 'Erro ao solicitar codigo: $e');
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
          );
    } catch (e) {
      setState(() => _erro = 'Codigo invalido ou expirado: $e');
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
                decoration: const InputDecoration(labelText: 'Codigo recebido'),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _nomeController,
                decoration: const InputDecoration(labelText: 'Nome (so no primeiro acesso)'),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _celularController,
                decoration: const InputDecoration(labelText: 'Celular (so no primeiro acesso)'),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _carregando ? null : _confirmarCodigo,
                child: Text(_carregando ? 'Confirmando...' : 'Confirmar e entrar'),
              ),
            ],
            if (_erro != null) ...[
              const SizedBox(height: 16),
              Text(_erro!, style: const TextStyle(color: Colors.red)),
            ],
          ],
        ),
      ),
    );
  }
}
